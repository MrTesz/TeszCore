import { defineConfig } from 'vitepress'
import { readFileSync } from 'fs'
import { resolve } from 'path'

const pom = readFileSync(resolve(__dirname, '../pom.xml'), 'utf-8')
const version = pom.match(/<version>(.*?)<\/version>/)[1]

// https://vitepress.dev/reference/site-config
export default defineConfig({
  title: "TeszCore",
  description: "Docs",
  base: '/TeszCore/',
  cleanUrls: true,
  themeConfig: {
    version,
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
