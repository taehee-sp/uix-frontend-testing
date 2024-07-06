// vitest.config.ts
import { defineConfig } from 'vitest/config';
import react from '@vitejs/plugin-react';
/// <reference types="vitest" />

export default defineConfig({
	plugins: [react()],
	root: './',
	test: {
		setupFiles: './setupTests.ts',
		include: ['./target/test/js/main.js'],
		css: true,
        globals: true,
		pool: 'vmThreads',
		poolOptions: {
			useAtomics: true
		},
		testTimeout: 3000,
		browser: {
			enabled: true,
			name: "chromium",
			headless: true,
			provider: "playwright"
		},
	},
});