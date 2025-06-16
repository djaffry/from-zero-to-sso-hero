import {afterEach, beforeEach, describe, expect, it, type Mock, vi} from 'vitest';
import {permissionCheckService} from '../permission-check.service';
import {Permission} from '../permission';
import * as sessionStore from '../session.store';

vi.mock('../session.store', () => ({
  getSessionInfoSync: vi.fn()
}));

describe('PermissionCheckService', () => {
  const mockGetSessionInfoSync = sessionStore.getSessionInfoSync as Mock;

  beforeEach(() => {
    vi.clearAllMocks();
  });

  afterEach(() => {
    vi.resetAllMocks();
  });

  describe('hasViewAccountInfoPermission', () => {
    it('should return true when user has VIEW permission', () => {
      // Arrange
      mockGetSessionInfoSync.mockReturnValue({
        user: {
          roles: [Permission.VIEW_ACCOUNT_INFO]
        }
      });

      // Act
      const result = permissionCheckService.hasViewAccountInfoPermission();

      // Assert
      expect(result).toBe(true);
      expect(mockGetSessionInfoSync).toHaveBeenCalledTimes(1);
    });

    it('should return false when user does not have VIEW permission', () => {
      // Arrange
      mockGetSessionInfoSync.mockReturnValue({
        user: {
          roles: ['SOME_OTHER_PERMISSION']
        }
      });

      // Act
      const result = permissionCheckService.hasViewAccountInfoPermission();

      // Assert
      expect(result).toBe(false);
      expect(mockGetSessionInfoSync).toHaveBeenCalledTimes(1);
    });

    it('should return false when user has no roles', () => {
      // Arrange
      mockGetSessionInfoSync.mockReturnValue({
        user: {
          roles: []
        }
      });

      // Act
      const result = permissionCheckService.hasViewAccountInfoPermission();

      // Assert
      expect(result).toBe(false);
      expect(mockGetSessionInfoSync).toHaveBeenCalledTimes(1);
    });

    it('should return false when user is undefined', () => {
      // Arrange
      mockGetSessionInfoSync.mockReturnValue(null);

      // Act
      const result = permissionCheckService.hasViewAccountInfoPermission();

      // Assert
      expect(result).toBe(false);
      expect(mockGetSessionInfoSync).toHaveBeenCalledTimes(1);
    });
  });

  describe('hasEditAccountInfoPermission', () => {
    it('should return true when user has EDIT permission', () => {
      // Arrange
      mockGetSessionInfoSync.mockReturnValue({
        user: {
          roles: [Permission.EDIT_ACCOUNT_INFO]
        }
      });

      // Act
      const result = permissionCheckService.hasEditAccountInfoPermission();

      // Assert
      expect(result).toBe(true);
      expect(mockGetSessionInfoSync).toHaveBeenCalledTimes(1);
    });

    it('should return false when user does not have EDIT permission', () => {
      // Arrange
      mockGetSessionInfoSync.mockReturnValue({
        user: {
          roles: ['SOME_OTHER_PERMISSION']
        }
      });

      // Act
      const result = permissionCheckService.hasEditAccountInfoPermission();

      // Assert
      expect(result).toBe(false);
      expect(mockGetSessionInfoSync).toHaveBeenCalledTimes(1);
    });

    it('should return false when user has no roles', () => {
      // Arrange
      mockGetSessionInfoSync.mockReturnValue({
        user: {
          roles: []
        }
      });

      // Act
      const result = permissionCheckService.hasEditAccountInfoPermission();

      // Assert
      expect(result).toBe(false);
      expect(mockGetSessionInfoSync).toHaveBeenCalledTimes(1);
    });

    it('should return false when user is undefined', () => {
      // Arrange
      mockGetSessionInfoSync.mockReturnValue(null);

      // Act
      const result = permissionCheckService.hasEditAccountInfoPermission();

      // Assert
      expect(result).toBe(false);
      expect(mockGetSessionInfoSync).toHaveBeenCalledTimes(1);
    });
  });
});
