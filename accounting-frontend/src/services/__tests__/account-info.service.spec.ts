import {afterEach, beforeEach, describe, expect, it, vi} from 'vitest';
import axios from 'axios';
import {oidcService} from '@/auth/oidc.service.ts';
import {accountInfoService} from "@/services/account-info.service.ts";
import {Permission} from "@/auth/permission.ts";

vi.mock('axios');
vi.mock('../../auth/oidc.service', () => ({
  oidcService: {
    getSessionInfo: vi.fn()
  }
}));

describe('AccountInfoService', () => {
  const mockSessionInfo = {
    tokens: {
      access: 'mock-access-token',
      refresh: 'mock-refresh-token',
      id: 'mock-id-token'
    },
    expiresAt: Date.now() + 3600000,
    user: {
      fullName: 'Test User',
      familyName: 'User',
      firstName: 'Test',
      username: 'testuser',
      email: 'test@example.com',
      roles: [Permission.VIEW_ACCOUNT_INFO, Permission.EDIT_ACCOUNT_INFO],
      padIntern: 123
    }
  };

  const mockAccountInfos = {
    content: [
      { id: '1', accountEntry: 'Test message 1' },
      { id: '2', accountEntry: 'Test message 2' }
    ],
    totalElements: 2,
    totalPages: 1,
    size: 10,
    number: 0
  };

  let consoleErrorSpy: any;

  beforeEach(() => {
    vi.clearAllMocks();
    vi.mocked(axios.get).mockResolvedValue({ data: mockAccountInfos });
    vi.mocked(axios.post).mockResolvedValue({ data: '3' });

    consoleErrorSpy = vi.spyOn(console, 'error').mockImplementation(() => {});
  });

  afterEach(() => {
    consoleErrorSpy.mockRestore();
  });

  describe('getAllAccountInfos', () => {
    it('should add authorization header with access token', async () => {
      // Arrange
      vi.mocked(oidcService.getSessionInfo).mockReturnValue(mockSessionInfo);

      // Act
      await accountInfoService.getAllAccountInfos();

      // Assert
      expect(axios.get).toHaveBeenCalledWith(
        'http://localhost:8050/api/accountinfos',
        {
          params: { page: 0, size: 10 },
          headers: { Authorization: 'Bearer mock-access-token' }
        }
      );
    });

    it('should throw error when user is not authenticated', async () => {
      // Arrange
      vi.mocked(oidcService.getSessionInfo).mockReturnValue(null);

      // Act & Assert
      await expect(accountInfoService.getAllAccountInfos()).rejects.toThrow('User not authenticated');
      expect(axios.get).not.toHaveBeenCalled();
    });

    it('should return paginated Account Info', async () => {
      // Arrange
      vi.mocked(oidcService.getSessionInfo).mockReturnValue(mockSessionInfo);

      // Act
      const result = await accountInfoService.getAllAccountInfos();

      // Assert
      expect(result).toEqual(mockAccountInfos);
    });

    it('should pass pagination parameters to API', async () => {
      // Arrange
      vi.mocked(oidcService.getSessionInfo).mockReturnValue(mockSessionInfo);

      // Act
      await accountInfoService.getAllAccountInfos(2, 20);

      // Assert
      expect(axios.get).toHaveBeenCalledWith(
        'http://localhost:8050/api/accountinfos',
        {
          params: { page: 2, size: 20 },
          headers: { Authorization: 'Bearer mock-access-token' }
        }
      );
    });

    it('should propagate API errors', async () => {
      // Arrange
      vi.mocked(oidcService.getSessionInfo).mockReturnValue(mockSessionInfo);
      vi.mocked(axios.get).mockRejectedValue(new Error('API error'));

      // Act & Assert
      await expect(accountInfoService.getAllAccountInfos()).rejects.toThrow('API error');
    });
  });

  describe('createAccountInfo', () => {
    it('should add authorization header with access token', async () => {
      // Arrange
      vi.mocked(oidcService.getSessionInfo).mockReturnValue(mockSessionInfo);

      // Act
      await accountInfoService.createAccountInfo('New message');

      // Assert
      expect(axios.post).toHaveBeenCalledWith(
        'http://localhost:8050/api/accountinfos',
        { accountEntry: 'New message' },
        {
          headers: { Authorization: 'Bearer mock-access-token' }
        }
      );
    });

    it('should throw error when user is not authenticated', async () => {
      // Arrange
      vi.mocked(oidcService.getSessionInfo).mockReturnValue(null);

      // Act & Assert
      await expect(accountInfoService.createAccountInfo('New message')).rejects.toThrow('User not authenticated');
      expect(axios.post).not.toHaveBeenCalled();
    });

    it('should return the created accountEntry ID', async () => {
      // Arrange
      vi.mocked(oidcService.getSessionInfo).mockReturnValue(mockSessionInfo);

      // Act
      const result = await accountInfoService.createAccountInfo('New message');

      // Assert
      expect(result).toEqual('3');
    });

    it('should propagate API errors', async () => {
      // Arrange
      vi.mocked(oidcService.getSessionInfo).mockReturnValue(mockSessionInfo);
      vi.mocked(axios.post).mockRejectedValue(new Error('API error'));

      // Act & Assert
      await expect(accountInfoService.createAccountInfo('New message')).rejects.toThrow('API error');
    });
  });
});
