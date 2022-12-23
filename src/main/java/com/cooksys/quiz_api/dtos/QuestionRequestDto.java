package com.cooksys.quiz_api.dtos;

import java.util.List;

import com.cooksys.quiz_api.entities.Question;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class QuestionRequestDto {

	  private String text;

	//  private Question ques;

	  private List<AnswerRequestDto> answers;

}
