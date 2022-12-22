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
	  
   // return quizMapper.entitiesToDtos(quizRepository.findAllByDeletedFalse());
    return quizMapper.entitiesToDtos(quizRepository.findAll());

  }

	@Override
	public ResponseEntity<QuizResponseDto> createQuiz(QuizRequestDto quizRequesttDto) {

		// to create a quiz
		// - you need questions, answers 
		
	//	name, List<Question> questions

		if(quizRequesttDto.getName() == null) {
				//|| quizRequesttDto.getQuestions() == null) {
			
			
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

		}

		// Map the quizRequestDto to quiz Entity
		Quiz quizToSave = quizMapper.requestDTO_To_Entity(quizRequesttDto);
		quizToSave.setName(quizRequesttDto.getName());
		quizToSave.setDeleted(false);

		//quizToSave.setQuestions(quizRequesttDto.getQuestions());


		return new ResponseEntity<>(quizMapper.entityToDto(quizRepository.saveAndFlush(quizToSave)), HttpStatus.OK);

	}

	@Override
	public QuizResponseDto deleteQuiz(Long id) {
		
		Quiz quizToDelete = getQuiz(id);
		quizToDelete.setDeleted(true);
				
		return quizMapper.entityToDto(quizRepository.saveAndFlush(quizToDelete));
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

	
	/*
	@Override
	  public QuizResponseDto add(Long id, QuestionRequestDto questionRequestDto) {

		if(questionRequestDto.getText() == null) {
			
			throw new BadRequestException("Question is required to be added to the quiz response dto");
		}
		
			
		Question questionToAdd = questionMapper.requestDto_To_Entity(questionRequestDto);	// constructing the question to add
		questionToAdd.setText(questionRequestDto.getText());		
		//System.out.println("--- > questionToAdd.getText() = " + questionRequestDto.getText());			

		// construct answers
		
		List<Answer> answerToAdd = answerMapper.requestList_To_List(questionToAdd.getAnswers());
		
		System.out.println("out of loop " + questionToAdd.getAnswers().size());
		for(int x = 0; x < questionToAdd.getAnswers().size(); x++) {
			
			
			//answerToAdd.get(x).setId(id);	// set id
			answerToAdd.get(x).setText(questionToAdd.getAnswers().get(x).getText());
			
			if(questionToAdd.getAnswers().size() > 0) {
				System.out.println("out of loop " + questionToAdd.getAnswers().size());
				break;
			}

		}
		
		questionToAdd.setAnswers(answerToAdd);

		
		List<Question> quesList = new ArrayList<Question>();	// not sure here
		quesList.add(questionToAdd);
		
		//List<Answer> answersForQuestion = answerRequestDto.getAnswers();  setting answers up 
		
		Quiz newQuiz = addQuestion(id, quesList);
				
		return quizMapper.entityToDto(quizRepository.saveAndFlush(newQuiz));
		
	} */
	
	
	@Override
	  public QuizResponseDto add(Long id, QuestionRequestDto questionRequestDto) {

		if(questionRequestDto.getText() == null) {
			
			System.out.println("question is null - resend question with text");

			throw new BadRequestException("Question is required to be added to the quiz response dto");
			
		}
		
		
		////////////////////////////////////////////////////////////////////////
		
		Quiz quizToFetch = getQuiz(id);		// fetch quiz by id
	    //int qtyQuizQuestions = quizToFetch.getQuestions().size();	// get number of questions in quiz 
	    
	    
	    Question questionToAdd = questionMapper.requestDto_To_Entity(questionRequestDto);	// constructing the question to add
		questionToAdd.setText(questionRequestDto.getText());   // not right
	    
	    
	    List<Question> quesList = new ArrayList<Question>();	// not sure here
		quesList.addAll(quizToFetch.getQuestions());
		quesList.add(questionToAdd);
				
	    quizToFetch.setQuestions(quesList);
	    
	    Quiz updatedQuiz = quizToFetch;
				
		return quizMapper.entityToDto(quizRepository.saveAndFlush(updatedQuiz));
		
			
		/////////////////////////////////////////////////////////////////////////
		
		
		//System.out.println("--- > questionToAdd.getText() = " + questionRequestDto.getText());			
		
		//questionToAdd.setAnswers(answerToAdd);
		
		
		
		
		//List<Answer> answersForQuestion = answerRequestDto.getAnswers();  setting answers up 
		
		//Quiz newQuiz = addQuestion(id, quesList);
		
	}

	@Override
	public QuestionResponseDto deleteQuestionFromQuiz(Long id, Long questionID) {


		Question questionToDelete = getQuestion(questionID);
		
		Quiz quizToDeleteFrom = getQuiz(id);
		// iterate thru the questions in quiz
		// check if question == questionToDelete
		// delete its answers
		// delete the question and return
		
		for(int i = 0; i < quizToDeleteFrom.getQuestions().size(); i++) {
			
			if(quizToDeleteFrom.getQuestions().get(i) == questionToDelete) {
				
				quizToDeleteFrom.getQuestions().get(i).setDeleted(true);
				
				quizToDeleteFrom.getQuestions().get(i).getAnswers().get(i).setDeleted(true);
				
				questionToDelete.setId(quizToDeleteFrom.getQuestions().get(i).getId());
				
			}
		}
		
				
		return questionMapper.entityToDto(questionRepository.saveAndFlush(questionToDelete));
						
		
	}
	
	private Question getQuestion(Long id) {		

		Optional<Question> optionalQuestion = questionRepository.findByIdAndDeletedFalse(id);
		
		if(!optionalQuestion.isPresent()) {
			

			//return new ResponseEntity<>(HttpStatus.NOT_FOUND);

			throw new NotFoundException("No question found with id: " + id);

		}
		
		return optionalQuestion.get();
		
	}
	

	
	/*
	
	 	 public Quiz addQuestion(Long id, List<Question> ques) {

			Quiz quizToUpdate = getQuiz(id);
			
			quizToUpdate.setQuestions(ques);
		 						
			return quizToUpdate;

	 }  */ 
	
	
	
	
	  
	

}










