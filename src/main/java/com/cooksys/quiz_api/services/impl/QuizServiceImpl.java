package com.cooksys.quiz_api.services.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.cooksys.quiz_api.dtos.AnswerRequestDto;
import com.cooksys.quiz_api.dtos.QuestionRequestDto;
import com.cooksys.quiz_api.dtos.QuestionResponseDto;
import com.cooksys.quiz_api.dtos.QuizRequestDto;
import com.cooksys.quiz_api.dtos.QuizResponseDto;
import com.cooksys.quiz_api.entities.Answer;
import com.cooksys.quiz_api.entities.Question;
import com.cooksys.quiz_api.entities.Quiz;
import com.cooksys.quiz_api.exceptions.BadRequestException;
import com.cooksys.quiz_api.exceptions.NotFoundException;
import com.cooksys.quiz_api.mappers.AnswerMapper;
import com.cooksys.quiz_api.mappers.QuestionMapper;
import com.cooksys.quiz_api.mappers.QuizMapper;
import com.cooksys.quiz_api.repositories.AnswerRepository;
import com.cooksys.quiz_api.repositories.QuestionRepository;
import com.cooksys.quiz_api.repositories.QuizRepository;
import com.cooksys.quiz_api.services.QuizService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

  private final AnswerRepository answerRepository;
  private final QuestionRepository questionRepository;
  private final QuizRepository quizRepository;
  private final QuizMapper quizMapper;
  private final QuestionMapper questionMapper;
  private final AnswerMapper answerMapper;

  @Override
  public List<QuizResponseDto> getAllQuizzes() {
	  
    return quizMapper.entitiesToDtos(quizRepository.findAllByDeletedFalse());
   // return quizMapper.entitiesToDtos(quizRepository.findAll());

  }

	@Override
	public ResponseEntity<QuizResponseDto> createQuiz(QuizResponseDto quizResponseDto) {


		if(quizResponseDto.getName() == null) {		
			
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

		}

 		Quiz quizToSave = quizMapper.requestDTO_To_Entity(quizResponseDto);
		quizToSave.setName(quizResponseDto.getName());
		quizToSave.setDeleted(false);
		
		quizRepository.saveAndFlush(quizToSave);

		for(Question q : quizToSave.getQuestions()) {	// set answers for each question
			
			q.setQuiz(quizToSave);
			q.setDeleted(false);
			questionRepository.saveAndFlush(q);
			
			for(Answer a : q.getAnswers()) {
		    	
		    	a.setQuestion(q);
		    	answerRepository.saveAndFlush(a);

		    }
			
		}
		

		return new ResponseEntity<>(quizMapper.entityToDto(quizRepository.saveAndFlush(quizToSave)), HttpStatus.OK);

	}

	@Override
	public QuizResponseDto deleteQuiz(Long id) {
		
		Quiz quizToDelete = getQuiz(id);
		
		for(Question q : quizToDelete.getQuestions()) {	// delete all questions for specified quiz
				
				q.setDeleted(true);
				questionRepository.saveAndFlush(q);
				
				for(Answer a : q.getAnswers()) {
			    	
			    	a.setDeleted(true);
			    	answerRepository.saveAndFlush(a);
	
			    }
				
			}
		
		
		quizToDelete.setDeleted(true);
				
		return quizMapper.entityToDto(quizRepository.saveAndFlush(quizToDelete));
		
		
		/*
		Quiz quizToDelete = getQuiz(id);
		quizToDelete.setDeleted(true);
				
		return quizMapper.entityToDto(quizRepository.saveAndFlush(quizToDelete));
		*/
	}

	
	
	private Quiz getQuiz(Long id) {		
		
		// if quiz doesn't exist in db, it sends 500 internal server error in postman with my NotFoundException


		Optional<Quiz> optionalQuiz = quizRepository.findByIdAndDeletedFalse(id);
		
		if(!optionalQuiz.isPresent()) {
			

			//return new ResponseEntity<>(HttpStatus.NOT_FOUND);

			throw new NotFoundException("No quiz found with id: " + id);

		}
		
		return optionalQuiz.get();
		
		//return new ResponseEntity<>(optionalQuiz.get(), HttpStatus.OK);

	}

	@Override
	public ResponseEntity<QuizResponseDto> getQuizById(Long id) {

		//return quizMapper.entityToDto(getQuiz(id));
		
		return new ResponseEntity<>(quizMapper.entityToDto(getQuiz(id)), HttpStatus.OK);

	
		 
	}

	@Override
	public QuizResponseDto rename(Long id, QuizRequestDto quizRequestDto) {

		// if i dont specify name, it sends bad request - with 500 internal server error in postman
		// if i try to update a quiz that doesnt exist, it sends - 500 internal server in postman (quiz not found)

		if(quizRequestDto.getName() == null) {
			
			throw new BadRequestException("Name is required on a quiz request dto");
		}
		
		Quiz quitToUpdate = getQuiz(id);
		quitToUpdate.setName(quizRequestDto.getName());
		
		return quizMapper.entityToDto(quizRepository.saveAndFlush(quitToUpdate));
		
	}

	
	@Override
	public QuestionResponseDto randomQuestion(Long id) {

		Quiz quizToFetch = getQuiz(id);		// fetch quiz by id
	    int qtyQuizQuestions = quizToFetch.getQuestions().size();	// get number of questions in quiz 
	    
	    System.out.println("size of quiz " + qtyQuizQuestions);
	    
	    Random r = new Random();
	    int low = 1;
	    int high = qtyQuizQuestions;
	    int randNum = r.nextInt(high-low) + low;
	    
	    //System.out.println("randNum " + randNum);
		
		Question randomQuestion = quizToFetch.getQuestions().get(randNum);		// fetch random question from list
		
		Long qId = randomQuestion.getId();
			
		//System.out.println("--- > qId " + qId);			
					
		return questionMapper.entityToDto(questionRepository.getById(qId));


	}
	
	// Adds a question to the specified quiz. Receives a Question, adds it with its respective answers
	
	@Override
	  public QuizResponseDto add(Long id, QuestionRequestDto questionRequestDto) {

		
		if(questionRequestDto.getText() == null || id == null) {
			
			//System.out.println("question is null - resend question with text");

			throw new BadRequestException("Question text and quiz id are required");
			
		}
						
		Quiz quizToFetch = getQuiz(id);		// fetch quiz by id	    
	    
	    Question questionToAdd = questionMapper.requestDto_To_Entity(questionRequestDto);	// constructing the question to add
		questionToAdd.setQuiz(quizToFetch);	    
	    questionRepository.saveAndFlush(questionToAdd);

	    
	    for(Answer a : questionToAdd.getAnswers()) {
	    	
	    	answerRepository.saveAndFlush(a);
	    	a.setQuestion(questionToAdd);

	    }
	    quizToFetch.getQuestions().add(questionToAdd);

				
		return quizMapper.entityToDto(quizRepository.saveAndFlush(quizToFetch));
		

		
	}

	@Override
	public QuestionResponseDto deleteQuestionFromQuiz(Long id, Long questionID) {

		/*
		if(id == null){
			
			throw new BadRequestException("Quiz id is required");
			
		}
		
		if(questionID == null){
			
			throw new BadRequestException("Question id is required");
			
		}*/
		
		Quiz quizToDeleteFrom = getQuiz(id);
		
		Question qToDel = questionRepository.getById(questionID);

		for(Question q : quizToDeleteFrom.getQuestions()) {
			
			if(q.getId() == questionID) {

				qToDel = questionRepository.getById(q.getId());

				for(Answer a : qToDel.getAnswers()) {
					
			    	//a.setQuestion(null);
					a.setDeleted(true);
					answerRepository.saveAndFlush(a);
				
				}
				qToDel.setDeleted(true);
				
			}

		}
		return questionMapper.entityToDto(questionRepository.saveAndFlush(qToDel));
				
		
	}
	
	/*
	private Question getQuestion(Long id) {		

		Optional<Question> optionalQuestion = questionRepository.findByIdAndDeletedFalse(id);
		
		if(!optionalQuestion.isPresent()) {
			

			//return new ResponseEntity<>(HttpStatus.NOT_FOUND);

			throw new NotFoundException("No question found with id: " + id);

		}
		
		return optionalQuestion.get();
		
	} */
	

	
	/*
	
	 	 public Quiz addQuestion(Long id, List<Question> ques) {

			Quiz quizToUpdate = getQuiz(id);
			
			quizToUpdate.setQuestions(ques);
		 						
			return quizToUpdate;

	 }  */ 
	
	
	
	
	  
	

}










