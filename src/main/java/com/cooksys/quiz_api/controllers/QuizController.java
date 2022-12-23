package com.cooksys.quiz_api.controllers;

import java.util.List;

import com.cooksys.quiz_api.dtos.AnswerRequestDto;
import com.cooksys.quiz_api.dtos.QuestionRequestDto;
import com.cooksys.quiz_api.dtos.QuestionResponseDto;
import com.cooksys.quiz_api.dtos.QuizRequestDto;
import com.cooksys.quiz_api.dtos.QuizResponseDto;
import com.cooksys.quiz_api.services.QuizService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/quiz")
public class QuizController {

  private final QuizService quizService;

  @GetMapping
  public List<QuizResponseDto> getAllQuizzes() {
    return quizService.getAllQuizzes();
  }
  
  // TODO: Implement the remaining 6 endpoints from the documentation.
  
  /***
   * POST quiz Creates a quiz and adds to collection
Returns the Quiz that it created
   */
  
  	@GetMapping("/{id}")
	public ResponseEntity<QuizResponseDto> getQuizById(@PathVariable Long id) {		// removing ResponseEntity here
		
		return quizService.getQuizById(id);		
	}
  
	@PostMapping  
	public ResponseEntity<QuizResponseDto> createQuiz(@RequestBody QuizRequestDto quiz) {
		/* @RequestBody tells spring it is receiving a JSON request - should be a quiz object */
		
		return quizService.createQuiz(quiz);
		
	}
 
	
	@DeleteMapping("/{id}")
	public QuizResponseDto deleteQuiz(@PathVariable Long id) {
		
		return quizService.deleteQuiz(id);
	}
	
	
	@PatchMapping("/{id}")
	public QuizResponseDto rename(@PathVariable Long id, @RequestBody QuizRequestDto quizRequestDto) {
		
		return quizService.rename(id, quizRequestDto);
		
	}
	
	
	@GetMapping("/{id}/random")
	public QuestionResponseDto randomQuestion(@PathVariable Long id) {		// removing ResponseEntity here
				
		return quizService.randomQuestion(id);
		
	}
	
	/*
	 * 
	 * PATCH quiz/{id}/add 
	 * 
	 * Adds a question to the specified quiz

		Receives a Question
		Returns the modified Quiz

	 */
	
	
	@PatchMapping("/{id}/add")
	public QuizResponseDto add(@PathVariable Long id, @RequestBody QuestionRequestDto questionRequestDto) {
		
		return quizService.add(id, questionRequestDto);
		
	}  
	
	
	
	/*
	 * DELETE quiz/{id}/delete/{questionID} Deletes the specified question from the specified quiz
Returns the deleted Question
	 */
	
		
	@DeleteMapping("/{id}/delete/{questionID}")
	public QuestionResponseDto deleteQuestionFromQuiz(@PathVariable Long id, QuizResponseDto quizResponseDtoLong, Long questionID) {
		
		return quizService.deleteQuestionFromQuiz(id, quizResponseDtoLong, questionID);
	}
	
	
}
