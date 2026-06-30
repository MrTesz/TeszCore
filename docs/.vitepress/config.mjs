import { defineConfig } from 'vitepress'

// https://vitepress.dev/reference/site-config
export default defineConfig({
  title: "TeszCore",
  description: "Docs",
  base: '/TeszCore/',
  cleanUrls: true,
  themeConfig: {
    // https://vitepress.dev/reference/default-theme-config
    search: {
      provider: 'local'
    },
    nav: [
      { text: 'Home', link: '/' },
      { text: 'Javadoc', link: 'https://javadoc.io/doc/io.github.mrtesz/teszcore/latest/index.html' }
    ],

    sidebar: [
      {
        text: 'Introduction',
        items: [
          { text: 'Home', link: '/' }
        ]
      },
      {
        text: 'Versions',
        items: [
          {
            text: 'Changelog',
            collapsed: true,
            link: '/changelog/changelog',
            items: [
              {text: '2.0.0', link: '/changelog/version/2.0.0'},
              {text: '2.0.1', link: '/changelog/version/2.0.1'},
              {text: '2.0.2', link: '/changelog/version/2.0.2'},
              {text: '2.0.3', link: '/changelog/version/2.0.3'},
              {text: '2.0.4', link: '/changelog/version/2.0.4'},
              {text: '2.0.5', link: '/changelog/version/2.0.5'},
              {text: '2.0.6', link: '/changelog/version/2.0.6'},
              {text: '2.1.0', link: '/changelog/version/2.1.0'},
              {text: '2.3.1', link: '/changelog/version/2.3.1'}
            ]
          },
          { text: 'Bugs', link: '/changelog/bugs' }
        ]
      },
      {
        text: 'Features',
        items: [
          { text: 'TeszCoreAPI', link: '/features/teszcoreapi' },
          { text: 'Database Managing', link: '/features/database-managing' },
          {
            text: 'Logging',
            link: '/features/logging',
            items: [
              { text: 'Logged Running', link: '/features/logged-running' }
            ]},
          { text: 'YAML', link: '/features/yaml' },
          { text: 'JSON', link: '/features/json' },
          { text: 'Random Generation', link: '/features/random-generation' },
          { text: 'Conditions', link: '/features/conditions' },
        ]
      }
    ],

    socialLinks: [
      { icon: 'github', link: 'https://github.com/MrTesz/TeszCore' }
    ],

    editLink: {
      pattern: `https://github.com/MrTesz/TeszCore/tree/main/docs/:path`,
      text: 'Edit this page on GitHub'
    }
  }
})
