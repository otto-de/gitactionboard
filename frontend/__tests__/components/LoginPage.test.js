import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { mount } from '../test-utils';
import LoginPage from '@/components/LoginPage.vue';

import { authenticate, fetchConfig } from '@/services/apiService';
import { isAuthenticate } from '@/services/authenticationService';
import {
  setAvailableAuths,
  setGithubCodeScanMonitoringEnabled,
  setGithubSecretsScanMonitoringEnabled,
  setVersion
} from '@/services/utils';

import flushPromises from 'flush-promises';
import Spinner from '@/components/Spinner.vue';
import getVuetify from '@/plugins/vuetify';

vi.mock('@/services/apiService');
vi.mock('@/services/authenticationService');
vi.mock('@/services/utils');

describe('<LoginPage />', () => {
  const defaultConfig = {
    availableAuths: ['BASIC_AUTH'],
    githubSecretsScanMonitoringEnabled: true,
    githubCodeScanMonitoringEnabled: true,
    version: '3.3.0'
  };

  const render = (mockRouterPush = vi.fn()) =>
    mount(LoginPage, {
      global: {
        plugins: [getVuetify()],
        mocks: {
          $router: {
            push: mockRouterPush
          }
        }
      }
    });

  afterEach(vi.clearAllMocks);

  it('should render spinner while fetching config', async () => {
    fetchConfig.mockResolvedValueOnce({
      availableAuths: ['BASIC_AUTH']
    });

    const vueWrapper = render();

    expect(vueWrapper.findComponent(Spinner).exists()).toBeTruthy();

    expect(vueWrapper.html()).toMatchSnapshot();

    await flushPromises();

    expect(vueWrapper.findComponent(Spinner).exists()).toBeFalsy();
    expect(fetchConfig).toHaveBeenCalledOnce();
  });

  it('should redirect to dashboard page if authentication is not configured', async () => {
    const config = {
      ...defaultConfig,
      availableAuths: [],
      githubSecretsScanMonitoringEnabled: false
    };
    fetchConfig.mockResolvedValueOnce(config);

    isAuthenticate.mockReturnValue(false);

    const mockRouterPush = vi.fn();
    const vueWrapper = render(mockRouterPush);

    await flushPromises();

    expect(vueWrapper.html()).toMatchSnapshot();

    expect(fetchConfig).toHaveBeenCalledOnce();
    expect(isAuthenticate).toHaveBeenCalledOnce();

    expect(setVersion).toHaveBeenCalledOnce();
    expect(setVersion).toHaveBeenCalledWith(config.version);

    expect(setAvailableAuths).toHaveBeenCalledOnce();
    expect(setAvailableAuths).toHaveBeenCalledWith(config.availableAuths);

    expect(setGithubSecretsScanMonitoringEnabled).toHaveBeenCalledOnce();
    expect(setGithubSecretsScanMonitoringEnabled).toHaveBeenCalledWith(config.githubSecretsScanMonitoringEnabled);
    expect(setGithubCodeScanMonitoringEnabled).toHaveBeenCalledOnce();
    expect(setGithubCodeScanMonitoringEnabled).toHaveBeenCalledWith(config.githubCodeScanMonitoringEnabled);
    expect(mockRouterPush).toHaveBeenCalledOnce();
    expect(mockRouterPush).toHaveBeenCalledWith('workflow-jobs');
  });

  it.each([
    [['BASIC_AUTH']],
    [['OAUTH2']],
    [['BASIC_AUTH', 'OAUTH2']]
  ])('should render login page only with auth mechanism %s', async (availableAuths) => {
    const config = {
      ...defaultConfig,
      availableAuths,
      githubCodeScanMonitoringEnabled: false
    };
    fetchConfig.mockResolvedValueOnce(config);

    isAuthenticate.mockReturnValue(false);

    const vueWrapper = render();

    await flushPromises();

    expect(vueWrapper.html()).toMatchSnapshot();

    expect(fetchConfig).toHaveBeenCalledOnce();
    expect(isAuthenticate).toHaveBeenCalledOnce();

    expect(setVersion).toHaveBeenCalledOnce();
    expect(setVersion).toHaveBeenCalledWith(config.version);

    expect(setAvailableAuths).toHaveBeenCalledOnce();
    expect(setAvailableAuths).toHaveBeenCalledWith(config.availableAuths);

    expect(setGithubSecretsScanMonitoringEnabled).toHaveBeenCalledOnce();
    expect(setGithubSecretsScanMonitoringEnabled).toHaveBeenCalledWith(config.githubSecretsScanMonitoringEnabled);
    expect(setGithubCodeScanMonitoringEnabled).toHaveBeenCalledOnce();
    expect(setGithubCodeScanMonitoringEnabled).toHaveBeenCalledWith(config.githubCodeScanMonitoringEnabled);
  });

  describe('Basic Auth', () => {
    let mockRouterPush;
    let vueWrapper;

    const username = 'admin';
    const password = 'password';

    const basicAuthLoginForm = '[test-id="basic-auth-login-form"]';
    const basicAuthLoginButton = '[test-id="basic-auth-login-button"]';
    const usernameInputField = '[test-id="username"]';
    const passwordInputField = '[test-id="password"]';
    const basicAuthAlert = '[test-id="basic-auth-alert"]';
    const vBtnDisabledClassName = 'v-btn--disabled';
    const submitPreventEventName = 'submit.prevent';

    beforeEach(async () => {
      const config = {
        ...defaultConfig,
        githubSecretsScanMonitoringEnabled: false,
        githubCodeScanMonitoringEnabled: false
      };
      fetchConfig.mockResolvedValueOnce(config);

      isAuthenticate.mockReturnValue(false);

      mockRouterPush = vi.fn();

      vueWrapper = render(mockRouterPush);

      await flushPromises();

      expect(vueWrapper.find(basicAuthLoginButton).classes(vBtnDisabledClassName)).toBeTruthy();
      await vueWrapper.find(usernameInputField).setValue(username);
      expect(vueWrapper.find(basicAuthLoginButton).classes(vBtnDisabledClassName)).toBeTruthy();
      await vueWrapper.find(passwordInputField).setValue(password);

      expect(vueWrapper.find(basicAuthLoginButton).classes(vBtnDisabledClassName)).toBeFalsy();
    });

    it('should redirect to dashboard post successful login', async () => {
      authenticate.mockResolvedValueOnce({});

      await vueWrapper.find(basicAuthLoginForm).trigger(submitPreventEventName);

      await flushPromises();

      expect(authenticate).toHaveBeenCalledOnce();
      expect(authenticate).toHaveBeenCalledWith(username, password);

      expect(mockRouterPush).toHaveBeenCalledOnce();
      expect(mockRouterPush).toHaveBeenCalledWith('workflow-jobs');
    });

    it('should not redirect to dashboard for unsuccessful login', async () => {
      authenticate.mockRejectedValueOnce('error');

      await vueWrapper.find(basicAuthLoginForm).trigger(submitPreventEventName);

      await flushPromises();

      expect(vueWrapper.find(basicAuthAlert).exists()).toBeTruthy();

      expect(authenticate).toHaveBeenCalledOnce();
      expect(authenticate).toHaveBeenCalledWith(username, password);

      expect(mockRouterPush).not.toBeCalled();
    });

    it('should hide alert once user provide new username post unsuccessful login', async () => {
      authenticate.mockRejectedValueOnce('error');

      await vueWrapper.find(basicAuthLoginForm).trigger(submitPreventEventName);

      await flushPromises();

      expect(vueWrapper.find(basicAuthAlert).exists()).toBeTruthy();

      await vueWrapper.find(usernameInputField).setValue(username + '_new');
      expect(vueWrapper.find(basicAuthAlert).exists()).toBeFalsy();
      expect(vueWrapper.find(basicAuthLoginButton).classes(vBtnDisabledClassName)).toBeFalsy();
    });

    it('should hide alert once user provide new password post unsuccessful login', async () => {
      authenticate.mockRejectedValueOnce('error');

      await vueWrapper.find(basicAuthLoginForm).trigger(submitPreventEventName);

      await flushPromises();

      expect(vueWrapper.find(basicAuthAlert).exists()).toBeTruthy();

      await vueWrapper.find(passwordInputField).setValue(password + '_new');
      expect(vueWrapper.find(basicAuthAlert).exists()).toBeFalsy();
      expect(vueWrapper.find(basicAuthLoginButton).classes(vBtnDisabledClassName)).toBeFalsy();
    });
  });
});
