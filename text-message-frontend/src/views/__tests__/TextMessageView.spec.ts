import {afterEach, beforeEach, describe, expect, it, vi} from 'vitest';
import {flushPromises, mount} from '@vue/test-utils';
import TextMessageView from '../TextMessageView.vue';
import {oidcService} from '@/auth/oidc.service.ts';
import {permissionCheckService} from '@/auth/permission-check.service.ts';
import {textMessageService} from '@/services/textmessage.service.ts';

vi.mock('../../auth/oidc.service', () => ({
  oidcService: {
    userIsLoggedIn: vi.fn()
  }
}));

vi.mock('../../auth/permission-check.service', () => ({
  permissionCheckService: {
    hasViewTextMessagesPermission: vi.fn(),
    hasEditTextMessagesPermission: vi.fn()
  }
}));

vi.mock('../../services/textmessage.service', () => ({
  textMessageService: {
    getAllTextMessages: vi.fn(),
    createTextMessage: vi.fn()
  }
}));

describe('TextMessageView', () => {
  let consoleErrorSpy: any;

  beforeEach(() => {
    vi.clearAllMocks();

    vi.mocked(oidcService.userIsLoggedIn).mockReturnValue(false);
    vi.mocked(permissionCheckService.hasViewTextMessagesPermission).mockReturnValue(false);
    vi.mocked(permissionCheckService.hasEditTextMessagesPermission).mockReturnValue(false);
    vi.mocked(textMessageService.getAllTextMessages).mockResolvedValue({
      content: [],
      totalElements: 0,
      totalPages: 0,
      size: 10,
      number: 0
    });

    consoleErrorSpy = vi.spyOn(console, 'error').mockImplementation(() => {});
  });

  afterEach(() => {
    consoleErrorSpy.mockRestore();
  });

  it('should show login prompt when user is not logged in', async () => {
    // Arrange
    vi.mocked(oidcService.userIsLoggedIn).mockReturnValue(false);

    // Act
    const wrapper = mount(TextMessageView);
    await flushPromises();

    // Assert
    expect(wrapper.find('.login-prompt').exists()).toBe(true);
    expect(wrapper.find('.messages-container').exists()).toBe(false);
    expect(wrapper.find('.permission-error').exists()).toBe(false);
  });

  it('should show permission error when user is logged in but has no view permission', async () => {
    // Arrange
    vi.mocked(oidcService.userIsLoggedIn).mockReturnValue(true);
    vi.mocked(permissionCheckService.hasViewTextMessagesPermission).mockReturnValue(false);

    // Act
    const wrapper = mount(TextMessageView);
    await flushPromises();

    // Assert
    expect(wrapper.find('.permission-error').exists()).toBe(true);
    expect(wrapper.find('.messages-container').exists()).toBe(false);
    expect(wrapper.find('.login-prompt').exists()).toBe(false);
  });

  it('should show messages but not the form when user has view permission but not edit permission', async () => {
    // Arrange
    vi.mocked(oidcService.userIsLoggedIn).mockReturnValue(true);
    vi.mocked(permissionCheckService.hasViewTextMessagesPermission).mockReturnValue(true);
    vi.mocked(permissionCheckService.hasEditTextMessagesPermission).mockReturnValue(false);
    vi.mocked(textMessageService.getAllTextMessages).mockResolvedValue({
      content: [{ id: '1', message: 'Test message' }],
      totalElements: 1,
      totalPages: 1,
      size: 10,
      number: 0
    });

    // Act
    const wrapper = mount(TextMessageView);
    await flushPromises();

    // Assert
    expect(wrapper.find('.messages-container').exists()).toBe(true);
    expect(wrapper.find('.permission-warning').exists()).toBe(true);
    expect(wrapper.find('.new-message-form').exists()).toBe(false);
    expect(wrapper.find('.message-card').exists()).toBe(true);
    expect(wrapper.text()).toContain('Test message');
    expect(wrapper.text()).toContain("You don't have permission to add new text messages");
  });

  it('should show messages and the form when user has both view and edit permissions', async () => {
    // Arrange
    vi.mocked(oidcService.userIsLoggedIn).mockReturnValue(true);
    vi.mocked(permissionCheckService.hasViewTextMessagesPermission).mockReturnValue(true);
    vi.mocked(permissionCheckService.hasEditTextMessagesPermission).mockReturnValue(true);
    vi.mocked(textMessageService.getAllTextMessages).mockResolvedValue({
      content: [{ id: '1', message: 'Test message' }],
      totalElements: 1,
      totalPages: 1,
      size: 10,
      number: 0
    });

    // Act
    const wrapper = mount(TextMessageView);
    await flushPromises();

    // Assert
    expect(wrapper.find('.messages-container').exists()).toBe(true);
    expect(wrapper.find('.new-message-form').exists()).toBe(true);
    expect(wrapper.find('.permission-warning').exists()).toBe(false);
    expect(wrapper.find('.message-card').exists()).toBe(true);
    expect(wrapper.text()).toContain('Test message');
  });

  it('should call createTextMessage when form is submitted', async () => {
    // Arrange
    vi.mocked(oidcService.userIsLoggedIn).mockReturnValue(true);
    vi.mocked(permissionCheckService.hasViewTextMessagesPermission).mockReturnValue(true);
    vi.mocked(permissionCheckService.hasEditTextMessagesPermission).mockReturnValue(true);
    vi.mocked(textMessageService.getAllTextMessages).mockResolvedValue({
      content: [],
      totalElements: 0,
      totalPages: 0,
      size: 10,
      number: 0
    });
    vi.mocked(textMessageService.createTextMessage).mockResolvedValue('1');

    // Act
    const wrapper = mount(TextMessageView);
    await flushPromises();

    // Fill in the form and submit
    await wrapper.find('input').setValue('New test message');
    await wrapper.find('button').trigger('click');
    await flushPromises();

    // Assert
    expect(textMessageService.createTextMessage).toHaveBeenCalledWith('New test message');
    expect(textMessageService.getAllTextMessages).toHaveBeenCalledTimes(2); // Initial load + after create
  });

  it('should show error message when API call fails', async () => {
    // Arrange
    vi.mocked(oidcService.userIsLoggedIn).mockReturnValue(true);
    vi.mocked(permissionCheckService.hasViewTextMessagesPermission).mockReturnValue(true);
    vi.mocked(permissionCheckService.hasEditTextMessagesPermission).mockReturnValue(true);
    vi.mocked(textMessageService.getAllTextMessages).mockRejectedValue(new Error('API error'));

    // Act
    const wrapper = mount(TextMessageView);
    await flushPromises();

    // Assert
    expect(wrapper.find('.error').exists()).toBe(true);
    expect(wrapper.text()).toContain('Failed to load messages');
  });
});
