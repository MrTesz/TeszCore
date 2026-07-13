import { defineConfig } from 'vitepress'

// https://vitepress.dev/reference/site-config
export default defineConfig({
  title: "TeszCore",
  description: "Docs",
  base: '/TeszCore/',
  cleanUrls: true,
  themeConfig: {
    version: "2.4.2",
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
      },
      {
        text: 'Versions',
        items: [
          {text: 'Changelog', link: '/changelog/changelog'},
          {text: 'Bugs', link: '/changelog/bugs'}
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
