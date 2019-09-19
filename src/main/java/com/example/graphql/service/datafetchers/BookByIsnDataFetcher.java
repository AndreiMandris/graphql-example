package com.example.graphql.service.datafetchers;

import com.example.graphql.dao.BookDao;
import com.example.graphql.model.Book;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BookByIsnDataFetcher implements DataFetcher<Book> {

    @Autowired
    private BookDao bookDao;

    @Override
    public Book get(DataFetchingEnvironment dataFetchingEnvironment) {
        return bookDao.getOne(dataFetchingEnvironment.getArgument("isn"));
    }
}
