import {afterEach, beforeEach, describe, expect, it, vi} from 'vitest';
import axios from 'axios';
import {textMessageService} from '../textmessage.service';
import {oidcService} from '@/auth/oidc.service.ts';

vi.mock('axios');
vi.mock('../../auth/oidc.service', () => ({
  oidcService: {
    getSessionInfo: vi.fn()
  }
}));

describe('TextMessageService', () => {
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
      roles: ['VIEW_TEXT_MESSAGES', 'EDIT_TEXT_MESSAGES'],
      padIntern: 123
    }
  };

  const mockTextMessages = {
    content: [
      { id: '1', message: 'Test message 1' },
      { id: '2', message: 'Test message 2' }
    ],
    totalElements: 2,
    totalPages: 1,
    size: 10,
    number: 0
  };

  let consoleErrorSpy: any;

  beforeEach(() => {
    vi.clearAllMocks();
    vi.mocked(axios.get).mockResolvedValue({ data: mockTextMessages });
    vi.mocked(axios.post).mockResolvedValue({ data: '3' });

    consoleErrorSpy = vi.spyOn(console, 'error').mockImplementation(() => {});
  });

  afterEach(() => {
    consoleErrorSpy.mockRestore();
  });

  describe('getAllTextMessages', () => {
    it('should add authorization header with access token', async () => {
      // Arrange
      vi.mocked(oidcService.getSessionInfo).mockReturnValue(mockSessionInfo);

      // Act
      await textMessageService.getAllTextMessages();

      // Assert
      expect(axios.get).toHaveBeenCalledWith(
        'http://localhost:8071/api/textmessages',
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
      await expect(textMessageService.getAllTextMessages()).rejects.toThrow('User not authenticated');
      expect(axios.get).not.toHaveBeenCalled();
    });

    it('should return paginated text messages', async () => {
      // Arrange
      vi.mocked(oidcService.getSessionInfo).mockReturnValue(mockSessionInfo);

      // Act
      const result = await textMessageService.getAllTextMessages();

      // Assert
      expect(result).toEqual(mockTextMessages);
    });

    it('should pass pagination parameters to API', async () => {
      // Arrange
      vi.mocked(oidcService.getSessionInfo).mockReturnValue(mockSessionInfo);

      // Act
      await textMessageService.getAllTextMessages(2, 20);

      // Assert
      expect(axios.get).toHaveBeenCalledWith(
        'http://localhost:8071/api/textmessages',
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
      await expect(textMessageService.getAllTextMessages()).rejects.toThrow('API error');
    });
  });

  describe('createTextMessage', () => {
    it('should add authorization header with access token', async () => {
      // Arrange
      vi.mocked(oidcService.getSessionInfo).mockReturnValue(mockSessionInfo);

      // Act
      await textMessageService.createTextMessage('New message');

      // Assert
      expect(axios.post).toHaveBeenCalledWith(
        'http://localhost:8071/api/textmessages',
        { message: 'New message' },
        {
          headers: { Authorization: 'Bearer mock-access-token' }
        }
      );
    });

    it('should throw error when user is not authenticated', async () => {
      // Arrange
      vi.mocked(oidcService.getSessionInfo).mockReturnValue(null);

      // Act & Assert
      await expect(textMessageService.createTextMessage('New message')).rejects.toThrow('User not authenticated');
      expect(axios.post).not.toHaveBeenCalled();
    });

    it('should return the created message ID', async () => {
      // Arrange
      vi.mocked(oidcService.getSessionInfo).mockReturnValue(mockSessionInfo);

      // Act
      const result = await textMessageService.createTextMessage('New message');

      // Assert
      expect(result).toEqual('3');
    });

    it('should propagate API errors', async () => {
      // Arrange
      vi.mocked(oidcService.getSessionInfo).mockReturnValue(mockSessionInfo);
      vi.mocked(axios.post).mockRejectedValue(new Error('API error'));

      // Act & Assert
      await expect(textMessageService.createTextMessage('New message')).rejects.toThrow('API error');
    });
  });
});
