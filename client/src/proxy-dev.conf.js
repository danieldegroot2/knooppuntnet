PROXY_CONFIG = {
  '/api/**': {
    target: 'http://localhost:9005',
    secure: false,
  },
  '/admin-api/**': {
    target: 'http://localhost:9005',
    secure: false,
  },
  '/websocket/*': {
    target: 'ws://localhost:9005',
    secure: false,
    ws: true,
  },
  '/tiles/**': {
    target: 'https://experimental.knooppuntnet.nl',
    changeOrigin: true,
    secure: false,
  },
  '/tiles-history/**': {
    target: 'https://experimental.knooppuntnet.nl',
    changeOrigin: true,
    secure: false,
  },
  '/images/**': {
    target: 'https://experimental.knooppuntnet.nl',
    changeOrigin: true,
    secure: false,
  },
  '/videos/**': {
    target: 'https://experimental.knooppuntnet.nl',
    changeOrigin: true,
    secure: false,
  },
  '/assets/**': {
    target: 'https://experimental.knooppuntnet.nl',
    changeOrigin: true,
    secure: false,
  },
};

module.exports = PROXY_CONFIG;
