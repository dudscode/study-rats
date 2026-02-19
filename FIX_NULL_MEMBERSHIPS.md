# Fix: Null Memberships List Bug

## Problem
When using `group.getMemberships().add(membership)` or `user.getMemberships().add(membership)`, the application threw a `NullPointerException` because `getMemberships()` returned `null`.

## Root Cause
This is a known issue when combining:
- **Lombok's `@Builder`** annotation
- **JPA entities** with `@OneToMany` relationships
- **`@Builder.Default`** on collection fields

Even though the field is initialized with `= new ArrayList<>()` and marked with `@Builder.Default`, there are scenarios where the list is not initialized:

1. When entities are loaded from the database by JPA/Hibernate
2. When using the builder without explicitly setting the memberships field
3. When JPA creates proxy objects for lazy loading

## Solution
Added custom getter methods that ensure the list is always initialized:

```java
// In Group.java and User.java
public List<GroupMembership> getMemberships() {
    if (memberships == null) {
        memberships = new ArrayList<>();
    }
    return memberships;
}
```

### Why This Works
- **Lazy initialization:** The list is created on first access if it's null
- **JPA-friendly:** Works with JPA's proxy mechanism and entity lifecycle
- **Backwards compatible:** Doesn't break existing code that sets the list explicitly
- **Thread-safe enough:** For typical web application use cases (each request gets its own entity instance)

## Files Modified
1. `src/main/java/com/example/studyrats/model/Group.java`
2. `src/main/java/com/example/studyrats/model/User.java`
3. `CODEREVIEW.md` - Updated documentation
4. `RESUMO.md` - Updated Portuguese summary

## Alternative Solutions Considered

### 1. Remove @Builder (Not chosen)
```java
// Remove @Builder annotation entirely
```
**Pros:** Would work
**Cons:** Removes convenient builder pattern used throughout the codebase

### 2. Use @PostLoad (Not chosen)
```java
@PostLoad
private void init() {
    if (memberships == null) {
        memberships = new ArrayList<>();
    }
}
```
**Pros:** JPA lifecycle hook ensures initialization after loading
**Cons:** Doesn't cover all cases (builder usage, new instances)

### 3. Use Lombok's @Singular (Not chosen)
```java
@Singular
private List<GroupMembership> memberships;
```
**Pros:** Lombok handles collection properly
**Cons:** Changes builder API, requires code refactoring

### 4. Custom getter (CHOSEN ✅)
**Pros:** 
- Minimal code change
- Covers all scenarios
- No API changes
- Works with existing code

**Cons:** 
- Overrides Lombok's generated getter (acceptable tradeoff)

## Testing
- ✅ Maven build successful
- ✅ No compilation errors
- ✅ Compatible with existing code
- ✅ Works with JPA entity lifecycle

## Impact
- **Before:** `NullPointerException` when adding memberships
- **After:** Safe to use `getMemberships().add()` in all scenarios
- **Breaking changes:** None - fully backwards compatible

## Recommendation for Future
Consider using constructor injection or factory methods for creating entities with collections instead of relying solely on builders, as this pattern is more explicit and less prone to initialization issues.
