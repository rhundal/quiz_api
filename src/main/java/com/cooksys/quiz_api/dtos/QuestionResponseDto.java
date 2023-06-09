package com.cooksys.quiz_api.dtos;

import java.util.List;

import com.cooksys.quiz_api.entities.Question;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class QuestionResponseDto {

  private Long id;

  private String text;

  //private Question question;
  
  private List<AnswerResponseDto> answers;

}
