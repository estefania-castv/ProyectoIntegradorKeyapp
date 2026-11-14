
module.exports = {
  testEnvironment: 'jsdom',

  roots: ['<rootDir>/src'],

  testMatch: ['**/?(*.)+(spec|test).[jt]s?(x)'],

  transform: {
    '^.+\\.[jt]sx?$': 'babel-jest',
  },

 moduleNameMapper: {
  '\\.(png|jpg|jpeg|gif|svg)$': '<rootDir>/test/fileMock.cjs',
  '\\.(css|less|scss)$': '<rootDir>/test/styleMock.cjs',
  '^@/(.*)$': '<rootDir>/src/$1',
},

  setupFilesAfterEnv: ['<rootDir>/test/setupTests.js'],

  clearMocks: true,
};

