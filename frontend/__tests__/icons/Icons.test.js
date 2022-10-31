import { describe, it, expect } from 'vitest';
import { mount } from '@vue/test-utils';
import InvalidIcon from '@/icons/InvalidIcon';
import LogoutIcon from '@/icons/LogoutIcon';
import CodeStandardViolationsIcon from '@/icons/CodeStandardViolationsIcon';
import SecretsIcon from '@/icons/SecretsIcon';
import SettingsIcon from '@/icons/SettingsIcon';
import WorkflowJobsIcon from '@/icons/WorkflowJobsIcon';
import HideOrShowIcon from '@/icons/HideOrShowIcon';
import HamburgerMenuIcon from '@/icons/HamburgerMenuIcon';

describe('Icons', () => {
  it.each([
    { component: InvalidIcon, name: 'InvalidIcon' },
    { component: LogoutIcon, name: 'LogoutIcon' },
    { component: CodeStandardViolationsIcon, name: 'CodeStandardViolationsIcon' },
    { component: SecretsIcon, name: 'SecretsIcon' },
    { component: SettingsIcon, name: 'SettingsIcon' },
    { component: WorkflowJobsIcon, name: 'WorkflowJobsIcon' }
  ])('should render $name', ({ component, name }) => {
    expect(mount(component).html()).toMatchSnapshot();
  });

  describe('HideOrShowIcon', () => {
    it('should render hide icon', () => {
      const wrapper = mount(HideOrShowIcon, {
        props: {
          displayHideButton: true
        }
      });
      expect(wrapper.html()).toMatchSnapshot();
    });
  });

  describe('HamburgerMenuIcon', () => {
    it('should render hamburger menu icon in closed state', () => {
      const wrapper = mount(HamburgerMenuIcon, {
        props: {
          isClicked: false
        }
      });
      expect(wrapper.html()).toMatchSnapshot();
    });

    it('should render hamburger menu icon in opened state', () => {
      const wrapper = mount(HamburgerMenuIcon, {
        props: {
          isClicked: true
        }
      });
      expect(wrapper.html()).toMatchSnapshot();
    });

    it('should emit clicked event', async () => {
      const wrapper = mount(HamburgerMenuIcon, {
        props: {
          isClicked: false
        }
      });

      await wrapper.find('.icon-container').trigger('click');
      expect(wrapper.emitted()).toHaveProperty('clicked');
    });
  });
});
