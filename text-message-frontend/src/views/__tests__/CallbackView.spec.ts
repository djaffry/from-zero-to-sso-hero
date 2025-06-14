import {afterEach, beforeEach, describe, expect, it, vi} from 'vitest';
import {flushPromises, mount} from '@vue/test-utils';
import {createRouter, createWebHistory} from 'vue-router';
import CallbackView from '../CallbackView.vue';
import {oidcService} from '@/auth/oidc.service';

vi.mock('@/auth/oidc.service', () => ({
  oidcService: {
    handleOidcCallback: vi.fn()
  }
}));

describe('CallbackView', () => {
  let router: any;
  let consoleErrorSpy: any;

  beforeEach(() => {
    vi.useFakeTimers();

    router = createRouter({
      history: createWebHistory(),
      routes: [
        {
          path: '/',
          name: 'home',
          component: { template: '<div>Home</div>' }
        },
        {
          path: '/callback',
          name: 'callback',
          component: CallbackView
        },
        {
          path: '/logout-callback',
          name: 'logout-callback',
          component: CallbackView
        }
      ]
    });

    Object.defineProperty(window, 'location', {
      value: {
        href: 'http://localhost:8070/callback?code=mock-code&state=mock-state'
      },
      writable: true
    });

    consoleErrorSpy = vi.spyOn(console, 'error').mockImplementation(() => {});
  });

  afterEach(() => {
    consoleErrorSpy.mockRestore();
    vi.clearAllMocks();
    vi.useRealTimers();
  });

  it('should handle login callback and redirect to home page', async () => {
    // Arrange
    vi.mocked(oidcService.handleOidcCallback).mockResolvedValue();

    router.push('/callback');
    await router.isReady();

    // Act
    const wrapper = mount(CallbackView, {
      global: {
        plugins: [router]
      }
    });
    await flushPromises();

    // Assert
    expect(oidcService.handleOidcCallback).toHaveBeenCalledWith(
      'http://localhost:8070/callback?code=mock-code&state=mock-state'
    );
    expect(router.currentRoute.value.path).toBe('/');
  });

  it('should handle logout callback and redirect to home page', async () => {
    // Arrange
    vi.mocked(oidcService.handleOidcCallback).mockResolvedValue();

    Object.defineProperty(window, 'location', {
      value: {
        href: 'http://localhost:8070/logout-callback'
      },
      writable: true
    });

    router.push('/logout-callback');
    await router.isReady();

    // Act
    const wrapper = mount(CallbackView, {
      global: {
        plugins: [router]
      }
    });
    await flushPromises();

    // Assert
    expect(oidcService.handleOidcCallback).toHaveBeenCalledWith(
      'http://localhost:8070/logout-callback'
    );
    expect(router.currentRoute.value.path).toBe('/');
  });

  it('should handle errors during callback processing and redirect to home page', async () => {
    // Arrange
    vi.mocked(oidcService.handleOidcCallback).mockRejectedValue(new Error('Callback error'));

    router.push('/callback');
    await router.isReady();

    // Act
    const wrapper = mount(CallbackView, {
      global: {
        plugins: [router]
      }
    });
    await flushPromises();

    // Assert
    expect(oidcService.handleOidcCallback).toHaveBeenCalled();
    expect(consoleErrorSpy).toHaveBeenCalled();
    expect(router.currentRoute.value.path).toBe('/');
  });

  it('should display loading message while processing', async () => {
    // Arrange
    let resolveCallback!: Function;
    const callbackPromise = new Promise<void>(resolve => {
      resolveCallback = resolve;
    });

    vi.mocked(oidcService.handleOidcCallback).mockReturnValue(callbackPromise);

    router.push('/callback');
    await router.isReady();

    // Act
    const wrapper = mount(CallbackView, {
      global: {
        plugins: [router]
      }
    });

    expect(wrapper.text()).toContain('Processing authentication');

    resolveCallback();

    await flushPromises();
    await vi.runAllTimersAsync();
    await flushPromises();

    expect(router.currentRoute.value.path).toBe('/');
  });
});
