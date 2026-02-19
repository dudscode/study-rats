# Code Review - Study Rats Project

## Executive Summary
This code review analyzes the first part of the Study Rats project, a Spring Boot application for managing study groups. The review identified several critical bugs, security concerns, and areas for improvement.

## Critical Issues Fixed ✅

### 1. Critical Business Logic Bug - Membership List Replacement
**Severity:** Critical  
**Location:** `GroupService.java:43` and `GroupMemberShipService.java:50`  
**Issue:** Using `List.of(membership)` replaced the entire membership list instead of adding to it.

**Before:**
```java
group.setMemberships(List.of(membership)); // Replaces all memberships!
```

**After:**
```java
group.getMemberships().add(membership); // Correctly adds to the list
```

**Impact:** This bug would have caused groups to only have one member at a time, losing all previous members when a new one joined.

### 2. Missing Validation
**Severity:** High  
**Location:** Entity classes and Controllers  
**Issue:** No validation on user inputs, which could allow invalid data into the database.

**Fixed by:**
- Added `@Valid` annotations to controller methods
- Added validation constraints to entities:
  - `@NotBlank` for required fields
  - `@Email` for email validation
  - `@Past` for birth dates
  - `@Size` for length constraints
  - Added `spring-boot-starter-validation` dependency

### 3. Encoding Issues
**Severity:** Medium  
**Location:** `application.properties`  
**Issue:** Portuguese characters with incorrect encoding prevented Maven from building the project.

**Fixed:** Replaced Portuguese comments with English equivalents.

### 4. Java Version Mismatch
**Severity:** High  
**Location:** `pom.xml`  
**Issue:** Project configured for Java 21, but runtime only has Java 17.

**Fixed:** Changed `java.version` from 21 to 17.

### 5. Missing @JsonBackReference Annotations
**Severity:** Medium  
**Location:** `GroupMembership.java`  
**Issue:** Bidirectional JPA relationships without proper JSON annotations can cause infinite recursion during serialization.

**Fixed:** Added `@JsonBackReference` annotations to both `user` and `group` fields in `GroupMembership`.

### 6. Inconsistent API Response Types
**Severity:** Low  
**Location:** `UserController.java`, `GroupController.java`  
**Issue:** Some endpoints returned raw entities exposing internal structure, others returned DTOs.

**Fixed:** 
- `UserController.create()` now returns `UserResponseDTO` instead of `User`
- `GroupController.getAllGroups()` now properly types the return as `List<GroupResponseDTO>` instead of `List<?>`

## Security Concerns ⚠️

### 1. Password Storage (Not Fixed - Requires Careful Design)
**Severity:** CRITICAL  
**Location:** `User.java:28`  
**Issue:** The field is named `passwordHash`, suggesting hashed passwords, but:
- No hashing is performed in `UserService.createUser()`
- Passwords are likely being stored as plaintext
- Password is returned in responses (via User entity)

**Recommendation:** 
- Implement password hashing using BCrypt (Spring Security's `BCryptPasswordEncoder`)
- Never return password hashes in DTOs
- Add a separate DTO for user creation that accepts plaintext password
- Remove `passwordHash` from `UserResponseDTO`

### 2. Database Credentials in Properties File
**Severity:** Medium  
**Location:** `application.properties`  
**Issue:** Database credentials are hardcoded and exposed in source control.

**Recommendation:** Use environment variables:
```properties
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/study_rats}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:}
```

### 3. DDL Auto Set to 'create'
**Severity:** High  
**Location:** `application.properties:7`  
**Issue:** `spring.jpa.hibernate.ddl-auto=create` drops and recreates all tables on startup.

**Recommendation:** 
- Use `validate` or `none` in production
- Use `update` only in development (with caution)
- Implement proper database migration with Flyway or Liquibase

## Code Quality Improvements Made ✅

### 1. Added Input Validation
- Entity-level validation with Jakarta Bean Validation
- Controller-level validation with `@Valid`
- Proper constraint messages

### 2. Improved Type Safety
- Removed wildcard return types (`List<?>`, `ResponseEntity<?>`)
- Made return types explicit and type-safe

### 3. Better JSON Handling
- Properly configured bidirectional relationships
- Prevents circular reference errors

## Additional Recommendations

### 1. Dependency Injection Pattern
**Current:** Field injection with `@Autowired`
```java
@Autowired
private UserService userService;
```

**Better:** Constructor injection (immutable, testable)
```java
private final UserService userService;

public UserController(UserService userService) {
    this.userService = userService;
}
```

### 2. Transaction Management
Add `@Transactional` annotations to service methods that modify data to ensure data integrity.

### 3. Exception Handling
Implement a global exception handler using `@ControllerAdvice` to return consistent error responses.

### 4. API Versioning
Consider adding version prefix to API paths: `/api/v1/users`, `/api/v1/groups`

### 5. Pagination
The `getAllUsers()` and `getAllGroups()` methods should support pagination for scalability.

### 6. Testing
No unit or integration tests were found. Consider adding:
- Unit tests for services
- Integration tests for controllers
- Repository tests with `@DataJpaTest`

### 7. Documentation
Add OpenAPI/Swagger documentation for the REST API.

### 8. Logging
Add proper logging statements for debugging and monitoring.

## Architecture Overview

The project follows a clean layered architecture:
- **Controllers:** Handle HTTP requests/responses
- **Services:** Business logic
- **Repositories:** Data access
- **Models:** JPA entities
- **DTOs:** Data transfer objects
- **Mappers:** Entity-to-DTO conversion

This separation of concerns is good practice and should be maintained.

## Summary of Changes Made

1. ✅ Fixed critical membership list bug in `GroupService` and `GroupMemberShipService`
2. ✅ Added validation annotations to `User` and `Group` entities
3. ✅ Added `@Valid` to controller methods
4. ✅ Added `spring-boot-starter-validation` dependency
5. ✅ Fixed encoding issues in `application.properties`
6. ✅ Changed Java version from 21 to 17
7. ✅ Added `@JsonBackReference` to `GroupMembership`
8. ✅ Made API return types consistent (all DTOs)
9. ✅ Improved `UserService.createUser()` to return DTO
10. ✅ Verified application builds successfully

## Next Steps for Development Team

1. **URGENT:** Implement password hashing before deployment
2. **HIGH:** Move database credentials to environment variables
3. **HIGH:** Change `ddl-auto` to appropriate value for environment
4. **MEDIUM:** Implement constructor injection pattern
5. **MEDIUM:** Add transaction management
6. **MEDIUM:** Implement global exception handling
7. **LOW:** Add unit and integration tests
8. **LOW:** Add API documentation
9. **LOW:** Implement pagination

## Conclusion

The code structure is well-organized with clear separation of concerns. The critical bugs have been fixed, and validation has been added. However, the password storage issue must be addressed before this application goes to production. With the recommended improvements, this will be a solid, production-ready application.
