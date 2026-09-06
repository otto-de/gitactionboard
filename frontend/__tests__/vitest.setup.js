import { afterEach, expect, vi } from 'vitest';
import vueSnapshotSerializer from 'vue3-snapshot-serializer';
import 'vitest-canvas-mock';
import { unmountAllWrappers } from './test-utils';

afterEach(() => {
  unmountAllWrappers();
});

class ResizeObserverStub {
  observe() { }
  unobserve() { }
  disconnect() { }
}

window.ResizeObserver = window.ResizeObserver || ResizeObserverStub;
expect.addSnapshotSerializer(vueSnapshotSerializer);
global.CSS = { supports: () => false };
process.env.TZ = 'Asia/Kolkata';
vi.stubGlobal('visualViewport', new EventTarget());
