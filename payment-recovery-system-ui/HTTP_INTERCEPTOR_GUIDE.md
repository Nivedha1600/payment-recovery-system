# HTTP Interceptor Guide

## Overview

The `AuthInterceptor` is an Angular HTTP interceptor that automatically handles authentication for all HTTP requests in the application.

## Responsibilities

### 1. Attach JWT Token to Requests

**What it does:**
- Intercepts all HTTP requests before they are sent
- Retrieves JWT token from `localStorage` via `TokenService`
- Adds `Authorization: Bearer <token>` header to every request

**How it works:**
```typescript
const token = this.tokenService.getToken();
if (token) {
  authRequest = request.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`
    }
  });
}
```

**Benefits:**
- No need to manually add token to each API call
- Consistent authentication across all requests
- Centralized token management

### 2. Handle 401 Unauthorized

**What it does:**
- Catches HTTP 401 (Unauthorized) responses
- Automatically handles session expiry
- Clears authentication data

**When it triggers:**
- Token expired
- Invalid token
- Token not provided
- User not authenticated

**Response:**
```typescript
if (error.status === 401) {
  this.handleUnauthorized();
}
```

### 3. Redirect to Login on Session Expiry

**What it does:**
- Clears all authentication data (token, role, username)
- Logs out user from `AuthService`
- Redirects to login page
- Preserves attempted URL for redirect after login
- Adds `sessionExpired` query parameter

**User Experience:**
1. User makes a request with expired token
2. Backend returns 401
3. Interceptor catches 401
4. User is automatically logged out
5. Redirected to `/auth/login?sessionExpired=true&returnUrl=/company/dashboard`
6. Login page shows "Your session has expired" message
7. After login, user is redirected to original page

## Implementation

### Location
`src/app/core/interceptors/auth.interceptor.ts`

### Registration
Registered in `CoreModule`:
```typescript
@NgModule({
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ]
})
```

### Dependencies
- `TokenService` - Manages JWT token storage
- `AuthService` - Manages authentication state
- `Router` - Handles navigation

## Request Flow

```
User Action
    ↓
Component calls API
    ↓
HTTP Request
    ↓
AuthInterceptor.intercept()
    ↓
Get token from TokenService
    ↓
Add Authorization header
    ↓
Send request to backend
    ↓
Backend validates token
    ↓
Response (200/401/etc.)
    ↓
If 401 → Handle unauthorized
    ↓
Return response to component
```

## Error Handling Flow

```
Backend returns 401
    ↓
AuthInterceptor catches error
    ↓
handleUnauthorized()
    ↓
Clear token (TokenService.clear())
    ↓
Logout user (AuthService.logout())
    ↓
Redirect to /auth/login
    ↓
Show session expired message
```

## Excluded Routes

The interceptor runs for **all** HTTP requests. If you need to exclude certain routes (e.g., public APIs), you can modify the interceptor:

```typescript
intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
  // Skip interceptor for public endpoints
  if (request.url.includes('/public/')) {
    return next.handle(request);
  }

  // ... rest of interceptor logic
}
```

## Testing

### Test Token Attachment
```typescript
// Verify token is added to request headers
expect(request.headers.get('Authorization')).toBe('Bearer test-token');
```

### Test 401 Handling
```typescript
// Mock 401 response
const errorResponse = new HttpErrorResponse({
  status: 401,
  statusText: 'Unauthorized'
});

// Verify logout and redirect are called
expect(authService.logout).toHaveBeenCalled();
expect(router.navigate).toHaveBeenCalledWith(['/auth/login'], ...);
```

## Best Practices

1. **Always use interceptor** - Don't manually add tokens in services
2. **Handle errors in components** - Interceptor handles 401, components handle other errors
3. **Test interceptor** - Ensure it works correctly with expired tokens
4. **Monitor token expiry** - Consider proactive token refresh before expiry

## Common Issues

### Issue: Token not being added
**Solution:** Check if token exists in localStorage and TokenService is working

### Issue: 401 not triggering redirect
**Solution:** Verify interceptor is registered in CoreModule and imported in AppModule

### Issue: Infinite redirect loop
**Solution:** Ensure login endpoint doesn't require authentication

## Security Notes

⚠️ **Important:**
- Interceptor adds token to **all** requests, including public endpoints
- Backend should validate tokens and return 401 for invalid/expired tokens
- Client-side token validation is for UX only, not security
- Always validate tokens on the backend

## Summary

The `AuthInterceptor` provides:
- ✅ Automatic JWT token attachment
- ✅ Centralized 401 error handling
- ✅ Automatic session expiry handling
- ✅ Seamless user experience
- ✅ Clean code (no manual token handling in services)

