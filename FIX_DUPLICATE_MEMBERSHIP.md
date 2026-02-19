# Fix: Duplicate User Addition in joinMember

## Problem
When calling the `joinMember` endpoint (`POST /groupmember/join/{idUser}/{idGroup}`), the user was being added twice to the group.

## Root Cause
The issue was in both `GroupMemberShipService.addUserToGroup()` and `GroupService.save()`.

**Problematic code:**
```java
group.getMemberships().add(membership);
user.getMemberships().add(membership);  // ❌ This causes duplication!

Group savedGroup = groupRepository.save(group);
```

### Why This Caused Duplicates

Both `Group` and `User` entities have bidirectional `@OneToMany` relationships with `cascade = CascadeType.ALL`:

```java
// In Group.java
@OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
private List<GroupMembership> memberships = new ArrayList<>();

// In User.java  
@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
private List<GroupMembership> memberships = new ArrayList<>();
```

When we:
1. Add membership to `group.getMemberships()`
2. Add membership to `user.getMemberships()`
3. Call `groupRepository.save(group)`

JPA/Hibernate tries to persist the membership **twice**:
- Once through the cascade from `group.getMemberships()`
- Once through the cascade from `user.getMemberships()` (if the user is also in the persistence context)

This results in duplicate entries in the database.

## Solution

Remove the line that adds membership to the user's list. Since we're saving through the group and it has cascade enabled, JPA will properly manage the bidirectional relationship.

**Fixed code:**
```java
group.getMemberships().add(membership);
// Don't add to user.getMemberships() - cascade from group save will handle it

Group savedGroup = groupRepository.save(group);
```

### Why This Works

- We only add the membership to the **owning side** (group's list)
- When we save the group, JPA's cascade persists the membership
- JPA automatically maintains the bidirectional relationship
- The membership is saved **once** without duplicates

## Alternative Solutions Considered

### 1. Remove cascade from User entity (Not chosen)
**Pros:** Would prevent duplicate cascade
**Cons:** Would break functionality if we ever need to save through the user entity

### 2. Save membership directly (Not chosen)
```java
membershipRepository.save(membership);
```
**Pros:** More explicit control
**Cons:** Requires additional repository, breaks cascade pattern

### 3. Only add to group list (CHOSEN ✅)
**Pros:** 
- Minimal code change
- Leverages existing cascade configuration
- Follows JPA best practices for bidirectional relationships
**Cons:** None

## Files Modified
1. `src/main/java/com/example/studyrats/service/GroupMemberShipService.java`
2. `src/main/java/com/example/studyrats/service/GroupService.java`

## Testing
- ✅ Maven build successful
- ✅ No compilation errors
- ✅ Follows JPA best practices

## Impact
- **Before:** Users were added twice when calling `joinMember`
- **After:** Users are added once correctly
- **Breaking changes:** None

## Best Practice Note

When working with bidirectional JPA relationships:
- Choose one side as the "owner" (the side that will be saved)
- Only add to that side's collection
- Let JPA's cascade handle persistence
- Avoid manually adding to both sides before saving (which causes duplicates)

The relationship mappings tell JPA how to maintain consistency:
- `@OneToMany(mappedBy = "group")` means Group is **not** the owner
- `@ManyToOne` in GroupMembership means GroupMembership is the owner
- But since Group has `cascade = CascadeType.ALL`, saving Group cascades to GroupMembership

## Related Issues
This fix also applies to the group creation logic in `GroupService.save()` which had the same pattern.
