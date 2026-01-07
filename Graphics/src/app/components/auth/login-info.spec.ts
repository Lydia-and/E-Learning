import { LoginInfo } from './login-info';

describe('LoginInfo', () => {
  it('should create an instance', () => {
     expect(new LoginInfo('testuser', 'testpassword')).toBeTruthy();
  });
});
