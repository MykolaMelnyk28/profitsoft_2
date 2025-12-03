package com.melnyk.profitsoft_2.service.impl;

import com.melnyk.profitsoft_2.config.props.PaginationProps;
import com.melnyk.profitsoft_2.dto.request.BookRequestDto;
import com.melnyk.profitsoft_2.dto.request.filter.impl.BookFilter;
import com.melnyk.profitsoft_2.dto.response.BookDetailsDto;
import com.melnyk.profitsoft_2.dto.response.BookInfoDto;
import com.melnyk.profitsoft_2.dto.response.PageDto;
import com.melnyk.profitsoft_2.dto.response.UploadResponse;
import com.melnyk.profitsoft_2.entity.Book;
import com.melnyk.profitsoft_2.entity.Genre;
import com.melnyk.profitsoft_2.exception.ResourceAlreadyExistsException;
import com.melnyk.profitsoft_2.exception.ResourceNotFoundException;
import com.melnyk.profitsoft_2.mapper.BookMapper;
import com.melnyk.profitsoft_2.repository.BookRepository;
import com.melnyk.profitsoft_2.service.AuthorService;
import com.melnyk.profitsoft_2.service.BookService;
import com.melnyk.profitsoft_2.service.GenreService;
import com.melnyk.profitsoft_2.service.ReportService;
import com.melnyk.profitsoft_2.util.PageUtil;
import com.melnyk.profitsoft_2.util.SpecificationFactory;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.MappingIterator;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
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
    private final ReportService<BookInfoDto> bookExcelReportService;
    private final ObjectMapper objectMapper;

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

    @Override
    @Transactional(readOnly = true)
    public void generateReport(BookFilter filter, HttpServletResponse response) throws IOException {
        String contentDispositionFormat = "attachment; filename=%s";
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDispositionFormat.formatted("report.xlsx"));

        Sort sort = PageUtil.parseSort(filter.sort(), paginationProps.getSort());
        Specification<Book> spec = SpecificationFactory.createForBook(filter);
        List<BookInfoDto> books = bookRepository.findAll(spec, sort).stream()
            .map(bookMapper::toInfoDto)
            .toList();

        OutputStream out = response.getOutputStream();
        bookExcelReportService.write(books, out);
        out.flush();
    }

    @Override
    @Transactional
    public UploadResponse uploadFromFile(MultipartFile file) throws IOException {
        UploadResponse response = new UploadResponse();
        List<UploadResponse.FailedItem> failedItems = response.getFailedItems();
        int createdCount = 0;

        MappingIterator<BookRequestDto> iter = objectMapper
            .readerFor(BookRequestDto.class)
            .readValues(file.getInputStream());

        while (iter.hasNext()) {
            BookRequestDto dto = null;
            dto = iter.next();

            try {
                Book book = createBook(dto);
                createdCount++;
            } catch (Exception e) {
                Throwable root = NestedExceptionUtils.getMostSpecificCause(e);
                failedItems.add(new UploadResponse.FailedItem(dto, root.getMessage()));
            }
        }
        iter.close();

        response.setCreatedCount(createdCount);
        response.setFailedCount(failedItems.size());
        response.setTotalCount(response.getCreatedCount() + failedItems.size());
        response.setFailedItems(failedItems);

        return response;
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
