package com.examly.springapp.model;

import lombok.Getter;
import lombok.Setter;

// One entry of faqs.json. Only used to read the seed file.
@Getter
@Setter
public class Faq {

    private Long id;
    private String category;
    private String question;
    private String answer;

    public Faq() {
    }
}
