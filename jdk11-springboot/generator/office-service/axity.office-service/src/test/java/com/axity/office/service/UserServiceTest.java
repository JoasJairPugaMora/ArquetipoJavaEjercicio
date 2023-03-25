package com.axity.office.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import com.axity.office.commons.dto.RoleDto;
import com.axity.office.commons.dto.UserDto;
import com.axity.office.commons.enums.ErrorCode;
import com.axity.office.commons.exception.BusinessException;
import com.axity.office.commons.request.PaginatedRequestDto;

/**
 * Class UserServiceTest
 * 
 * @author username@axity.com
 */
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Transactional
class UserServiceTest
{
  private static final Logger LOG = LoggerFactory.getLogger( UserServiceTest.class );

  @Autowired
  private UserService userService;

  /**
   * Method to validate the paginated search
   */
  @Test
  void testFindUsers()
  {
    var request = new PaginatedRequestDto();
    request.setLimit( 5 );
    request.setOffset( 0 );
    var users = this.userService.findUsers( request );

    LOG.info( "Response: {}", users );

    assertNotNull( users );
    assertNotNull( users.getData() );
    assertFalse( users.getData().isEmpty() );
  }

  /**
   * Method to validate the search by id
   * 
   * @param userId
   */
  @ParameterizedTest
  @ValueSource(ints = { 1 })
  void testFind( Integer userId ) {
    var user = this.userService.find( userId );
    assertNotNull( user );
    LOG.info( "Response: {}", user );
  }

  /**
   * Method to validate the search by id inexistent
   */
  @Test
  void testFind_NotExists()
  {
    var user = this.userService.find( 999999 );
    assertNull( user );
  }

  /**
   * Test method for
   * {@link com.axity.office.service.impl.UserServiceImpl#create(com.axity.office.commons.dto.UserDto)}.
   */
  @Test
  void testCreate()
  {
    var dto = new UserDto();    
    // Crear de acuerdo a la entidad
    var list = new ArrayList<RoleDto>();
    list.add(createRole(1));
    list.add(createRole(2));

    dto.setUsername("JoasPuga");
    dto.setEmail("joas.puga@axity.com");
    dto.setName("Joas");
    dto.setLastName("Puga");
    dto.setRoles(list);

    var response = this.userService.create( dto );
    assertNotNull( response );
    assertEquals( 0, response.getHeader().getCode() );
    assertNotNull( response.getBody() );

    this.userService.delete( dto.getId() );
  }

  /**
  * Test method for createUserWithOneRole
  * {@link com.axity.office.service.impl.UserServiceImpl#create(com.axity.office.commons.dto.UserDto)}.
  */
  @Test
  void createUserWithOneRole(){
    var list = new ArrayList<RoleDto>();
    list.add(createRole(1));
    
    var dto = new UserDto();
    dto.setUsername("JoasJair");
    dto.setEmail("joasjair@mail.com");
    dto.setName("Joas Jair");
    dto.setLastName("Puga");
    dto.setRoles(list); 

    var response = this.userService.create( dto );
    assertNotNull( response );
    assertEquals( 0, response.getHeader().getCode() );
    assertNotNull( response.getBody() );

    this.userService.delete(dto.getId());
  }

  private RoleDto createRole(int id){
    var role = new RoleDto();
    role.setId(id);
    return role;
  }

  /**
  * Test method for createUserWithManyRoles
  * {@link com.axity.office.service.impl.UserServiceImpl#create(com.axity.office.commons.dto.UserDto)}.
  */
  @Test
  void createUserWithManyRoles(){
    var list = new ArrayList<RoleDto>();
    list.add(createRole(1));
    list.add(createRole(2));
    list.add(createRole(3));
    
    var dto = new UserDto();
    dto.setUsername("JoasJair");
    dto.setEmail("joasjair@mail.com");
    dto.setName("Joas Jair");
    dto.setLastName("Puga");
    dto.setRoles(list); 

    var response = this.userService.create( dto );
    assertNotNull( response );
    assertEquals( 0, response.getHeader().getCode() );
    assertNotNull( response.getBody() );

    this.userService.delete(dto.getId());
  }

  /**
  * Test method for createUserWithRoleInexistent
  * {@link com.axity.office.service.impl.UserServiceImpl#create(com.axity.office.commons.dto.UserDto)}.
  */
  @Test
  void createUserWithRoleInexistent(){
    var list = new ArrayList<RoleDto>();
    list.add(createRole(1001));
    
    var dto = new UserDto();
    dto.setUsername("JoasJair");
    dto.setEmail("joasjair@mail.com");
    dto.setName("Joas Jair");
    dto.setLastName("Puga");
    dto.setRoles(list); 

    var response = this.userService.create( dto );
    assertNotNull( response );
    assertEquals( ErrorCode.ROLE_NOT_FOUND.getCode(), response.getHeader().getCode());
  }

  /**
  * Test method for createUserWithEmptyRole
  * {@link com.axity.office.service.impl.UserServiceImpl#create(com.axity.office.commons.dto.UserDto)}.
  */
  @Test
  void createUserWithEmptyRole(){
    var list = new ArrayList<RoleDto>();
    
    var dto = new UserDto();
    dto.setUsername("JoasJair");
    dto.setEmail("joasjair@mail.com");
    dto.setName("Joas Jair");
    dto.setLastName("Puga");
    dto.setRoles(list); 

    var response = this.userService.create( dto );
    assertNotNull( response );
    assertEquals( ErrorCode.NOT_ROLE_SELECTED.getCode(), response.getHeader().getCode());
  }

  /**
   * Method to validate if username already exist
   * {@link com.axity.office.service.impl.UserServiceImpl#create(com.axity.office.commons.dto.UserDto)}.
   */
  @Test
  void testValidateUsernameAlreadyExist(){
    var list = new ArrayList<RoleDto>();
    list.add(createRole(1));
    list.add(createRole(2));
    
    var dto = new UserDto();
    dto.setUsername("jonah.stephens");
    dto.setEmail("joaspuga@company.net");
    dto.setName("Jair");
    dto.setLastName("Mora");
    dto.setRoles(list); 

    var response = this.userService.create( dto );
    assertNotNull( response );
    assertEquals( ErrorCode.USERNAME_ALREADY_EXISTS.getCode(), response.getHeader().getCode());
    assertNull(response.getBody());
    
  }

  /**
   * Method to validate if email already exist
   * {@link com.axity.office.service.impl.UserServiceImpl#create(com.axity.office.commons.dto.UserDto)}.
   */
  @Test
  void testValidateEmailAlreadyExist(){
    var list = new ArrayList<RoleDto>();
    list.add(createRole(1));
    list.add(createRole(2));
    
    var dto = new UserDto();
    dto.setUsername("Username1");
    dto.setEmail("gillian.bowers@company.net");
    dto.setName("User");
    dto.setLastName("Name");
    dto.setRoles(list); 

    var response = this.userService.create( dto );
    assertNotNull( response );
    assertEquals( ErrorCode.EMAIL_ALREADY_EXISTS.getCode(), response.getHeader().getCode());
    assertNull(response.getBody());
    
  }

  /**
   * Method to validate update
   */
  @Test
  void testUpdate()
  {
    var user = this.userService.find( 1 ).getBody();
    // TODO: actualizar de acuerdo a la entidad
    String name = "Joas";
    user.setName(name);

    var response = this.userService.update( user );

    assertNotNull( response );
    assertEquals( 0, response.getHeader().getCode() );
    assertTrue( response.getBody() );
    user = this.userService.find( 1 ).getBody();

    // Verificar que se actualice el valor
    assertEquals(name, user.getName());
  }

  /**
   * Method to validate an inexistent registry
   */
  @Test
  void testUpdate_NotFound()
  {
    var user = new UserDto();
    user.setId(999999);
    var ex = assertThrows( BusinessException.class, () -> this.userService.update( user ) );

    assertEquals( ErrorCode.OFFICE_NOT_FOUND.getCode(), ex.getCode() );
  }

  /**
   * Test method for {@link com.axity.office.service.impl.UserServiceImpl#delete(java.lang.String)}.
   */
  @Test
  void testDeleteNotFound()
  {
    var ex = assertThrows( BusinessException.class, () -> this.userService.delete( 999999 ) );
    assertEquals( ErrorCode.OFFICE_NOT_FOUND.getCode(), ex.getCode() );
  }
}
