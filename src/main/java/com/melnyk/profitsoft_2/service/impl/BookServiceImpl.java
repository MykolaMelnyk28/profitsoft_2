package com.melnyk.profitsoft_2.service.impl;

import com.melnyk.profitsoft_2.config.props.PaginationProps;
import com.melnyk.profitsoft_2.dto.request.BookRequestDto;
import com.melnyk.profitsoft_2.dto.request.filter.impl.BookFilter;
import com.melnyk.profitsoft_2.dto.response.BookDetailsDto;
import com.melnyk.profitsoft_2.dto.response.BookInfoDto;
import com.melnyk.profitsoft_2.dto.response.PageDto;
import com.melnyk.profitsoft_2.entity.Book;
import com.melnyk.profitsoft_2.entity.Genre;
import com.melnyk.profitsoft_2.exception.ResourceAlreadyExistsException;
import com.melnyk.profitsoft_2.exception.ResourceNotFoundException;
import com.melnyk.profitsoft_2.mapper.BookMapper;
import com.melnyk.profitsoft_2.repository.BookRepository;
import com.melnyk.profitsoft_2.service.AuthorService;
import com.melnyk.profitsoft_2.service.BookService;
import com.melnyk.profitsoft_2.service.GenreService;
import com.melnyk.profitsoft_2.util.PageUtil;
import com.melnyk.profitsoft_2.util.SpecificationFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final TransactionTemplate transactionTemplate;
    private final BookMapper bookMapper;
    private final AuthorService authorService;
    private final GenreService genreService;
    private final PaginationProps paginationProps;

    @Override
    public BookDetailsDto create(BookRequestDto body) throws ResourceAlreadyExistsException {
        log.info("Creating book {}", body);
        Book created = transactionTemplate.execute(status -> createBook(body));
        log.info("Book created id={}", created.getId());
        return bookMapper.toDetailsDto(created);
    }

    @Override
    @Transactional(readOnly = true)
    public BookDetailsDto getById(Long id) throws ResourceNotFoundException {
        log.info("Getting book id={}", id);
        BookDetailsDto dto = bookMapper.toDetailsDto(getByIdOrThrow(id));
        log.info("Book found id={}", id);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public Book getByIdOrThrow(Long id) throws ResourceNotFoundException {
        return bookRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("%d not found".formatted(id), id, "Book"));
    }

    @Override
    @Transactional(readOnly = true)
    public PageDto<BookInfoDto> search(BookFilter filter) {
        log.info("Searching books by filter {}", filter);
        Pageable pageable = PageUtil.pageableFrom(filter, paginationProps);
        Specification<Book> spec = SpecificationFactory.createForBook(filter);
        Page<Book> page = bookRepository.findAll(spec, pageable);
        log.info("{}", page);
        return new PageDto<>(page.map(bookMapper::toInfoDto));
    }

    @Override
    public BookDetailsDto updateById(Long id, BookRequestDto body)
        throws ResourceNotFoundException, ResourceAlreadyExistsException {
        log.info("Updating book id={}", id);

        Book updated = transactionTemplate.execute(status -> updateBook(id, body));

        log.info("Updated book id={}", id);
        return bookMapper.toDetailsDto(updated);
    }

    @Override
    @Transactional
    public void deleteById(Long id) throws ResourceNotFoundException {
        log.info("Deleting book id={}", id);
        getByIdOrThrow(id);
        bookRepository.deleteById(id);
        log.info("Deleted book id={}", id);
    }

    private Book createBook(BookRequestDto body) throws ResourceAlreadyExistsException {
        checkUniqueFieldsOrThrow(body);
        Book book = bookMapper.toEntity(body);
        book.setAuthor(authorService.getByIdOrThrow(body.authorId()));
        book.getGenres().addAll(getGenresByIds(body.genreIds()));
        return bookRepository.save(book);
    }

    private Book updateBook(Long id, BookRequestDto body) {
        Book found = getByIdOrThrow(id);

        boolean isUpdated = false;

        if (!Objects.equals(body.title(), found.getTitle())) {
            found.setTitle(body.title());
            isUpdated = true;
        }

        if (!Objects.equals(body.description(), found.getDescription())) {
            found.setDescription(body.description());
            isUpdated = true;
        }

        if (!Objects.equals(body.yearPublished(), found.getYearPublished())) {
            found.setYearPublished(body.yearPublished());
            isUpdated = true;
        }

        if (!Objects.equals(body.pages(), found.getPages())) {
            found.setPages(body.pages());
            isUpdated = true;
        }

        if (!Objects.equals(body.authorId(), found.getAuthor().getId())) {
            checkUniqueFieldsOrThrow(body);
            found.setAuthor(authorService.getByIdOrThrow(body.authorId()));
            isUpdated = true;
        }

        Set<Genre> genres = found.getGenres();
        Set<Long> existingGenreIds = genres.stream().map(Genre::getId).collect(Collectors.toSet());
        if (!existingGenreIds.equals(body.genreIds())) {
            genres.clear();
            genres.addAll(getGenresByIds(body.genreIds()));
            isUpdated = true;
        }

        if (isUpdated) {
            return bookRepository.save(found);
        }
        return found;
    }

    @Transactional(readOnly = true)
    private void checkUniqueFieldsOrThrow(BookRequestDto body) throws ResourceNotFoundException {
        Objects.requireNonNull(body);
        Optional<Book> opt = bookRepository.findByTitleAndAuthorId(body.title(), body.authorId());
        if (opt.isPresent()) {
            Book book = opt.get();
            throw new ResourceAlreadyExistsException(
                "The title and authorId combination already exists",
                book.getId(), "Book", List.of("title", "authorId"));
        }
    }

    @Transactional(readOnly = true)
    private List<Genre> getGenresByIds(Set<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        List<Genre> foundGenres = genreService.getAllByIds(ids);

        Set<Long> foundIds = foundGenres.stream()
            .map(Genre::getId)
            .collect(Collectors.toSet());

        List<Long> missingIds = ids.stream()
            .filter(id -> !foundIds.contains(id))
            .toList();

        if (!missingIds.isEmpty()) {
            throw new ResourceNotFoundException("Genres not found", missingIds, "Genre");
        }

        return foundGenres;
    }

}
