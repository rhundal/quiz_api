package com.cooksys.quiz_api.services;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.cooksys.quiz_api.dtos.AnswerRequestDto;
import com.cooksys.quiz_api.dtos.QuestionRequestDto;
import com.cooksys.quiz_api.dtos.QuestionResponseDto;
import com.cooksys.quiz_api.dtos.QuizRequestDto;
import com.cooksys.quiz_api.dtos.QuizResponseDto;
import com.cooksys.quiz_api.entities.Answer;
import com.cooksys.quiz_api.entities.Question;
import com.cooksys.quiz_api.entities.Quiz;

public interface QuizService {

  List<QuizResponseDto> getAllQuizzes();

  ResponseEntity<QuizResponseDto> createQuiz(QuizRequestDto quiz);

  QuizResponseDto deleteQuiz(Long id);

  //QuizResponseDto getQuizById(Long id);
  
  ResponseEntity<QuizResponseDto> getQuizById(Long id);

  QuizResponseDto rename(Long id, QuizRequestDto quizRequestDto);

  QuestionResponseDto randomQuestion(Long id);

  QuizResponseDto add(Long id, QuestionRequestDto questionRequestDto);

  QuestionResponseDto deleteQuestionFromQuiz(Long id, QuizResponseDto quizResponseDto, Long questionID);
  
 // Quiz addQuestion(Long id, List<Question> text);

  

}
