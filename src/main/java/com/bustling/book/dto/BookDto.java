package com.bustling.book.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookDto {
    private int transType;
    private String bookTitle;
    private String writers;
    private String publisher;
    private String genre;
    private int status;
    private String memo;
}
