import { SheriffConfig } from '@softarc/sheriff-core';

// noinspection JSUnusedGlobalSymbols
export const sheriffConfig: SheriffConfig = {
  version: 1,
  tagging: {
    'libs/settings/src/lib': ['settings'],
    'libs/analysis/src/lib/changes': ['analysis/changes'],
    'libs/analysis/src/lib/route': ['analysis/route'],
    'libs/analysis/src/lib/network': ['analysis/network'],
    'libs/analysis/src/lib/overview': ['analysis/overview'],
    'libs/analysis/src/lib/node': ['analysis/node'],
    'libs/analysis/src/lib/facts': ['analysis/facts'],
    'libs/analysis/src/lib/analysis': ['analysis/analysis'],
    'libs/analysis/src/lib/subset': ['analysis/subset'],
    'libs/analysis/src/lib/strategy': ['analysis/strategy'],
    'libs/analysis/src/lib/components/changes/route': [
      'analysis/components/changes/route',
    ],
    'libs/analysis/src/lib/components/changes/node': [
      'analysis/components/changes/node',
    ],
    'libs/analysis/src/lib/components/changes/filter': [
      'analysis/components/changes/filter',
    ],
    'libs/analysis/src/lib/components/changes': ['analysis/components/changes'],
    'libs/analysis/src/lib/components/indicators/route': [
      'analysis/components/indicators/route',
    ],
    'libs/analysis/src/lib/components/change-set/components': [
      'analysis/components/change-set/components',
    ],
    'libs/analysis/src/lib/components/change-set': [
      'analysis/components/change-set',
    ],
    'libs/analysis/src/lib/components/filter': ['analysis/components/filter'],
    'libs/analysis/src/lib/components/edit': ['analysis/components/edit'],
    'libs/analysis/src/lib/components': ['analysis/components'],
    'libs/analysis/src/lib/fact': ['analysis/fact'],
    'libs/analysis/src/lib/changeset': ['analysis/changeset'],
    'libs/analysis/src/lib/location': ['analysis/location'],
    'libs/planner/src/lib': ['planner'],
    'libs/status/src/lib': ['status'],
    'libs/shared/src/lib/base': ['base'],
    'libs/shared/src/lib/spinner': ['spinner'],
    'libs/shared/src/lib/core': ['core'],
    'libs/poi/src/lib': ['poi'],
    'libs/demo/src/lib': ['demo'],
    'libs/shared/src/lib/services': ['services'],
    'libs/util/src': ['util'],

    'libs/shared/src/lib/components/shared/tags': ['components/shared/tags'],
    'libs/shared/src/lib/components/shared/json': ['components/shared/json'],
    'libs/shared/src/lib/components/shared/link': ['components/shared/link'],
    'libs/shared/src/lib/components/shared/items': ['components/shared/items'],
    'libs/shared/src/lib/components/shared/day': ['components/shared/day'],
    'libs/shared/src/lib/components/shared/dialog': [
      'components/shared/dialog',
    ],
    'libs/shared/src/lib/components/shared/paginator': [
      'components/shared/paginator',
    ],
    'libs/shared/src/lib/components/shared/toolbar': [
      'components/shared/toolbar',
    ],
    'libs/shared/src/lib/components/shared/icon': ['components/shared/icon'],
    'libs/shared/src/lib/components/shared/indicator': [
      'components/shared/indicator',
    ],
    'libs/shared/src/lib/components/shared/error': ['components/shared/error'],
    'libs/shared/src/lib/components/shared/data': ['components/shared/data'],
    'libs/shared/src/lib/components/shared/page': ['components/shared/page'],
    'libs/shared/src/lib/components/shared/format': [
      'components/shared/format',
    ],
    'libs/shared/src/lib/components/shared/timestamp': [
      'components/shared/timestamp',
    ],
    'libs/shared/src/lib/components/shared/sidebar': [
      'components/shared/sidebar',
    ],
    'libs/shared/src/lib/components/shared/menu': ['components/shared/menu'],
    'libs/shared/src/lib/components/shared': ['components/shared'],
    'libs/shared/src/lib/components/poi': ['components/poi'],

    'libs/ol/src': {
      'lib/layers': ['ol'],
      'lib/style': ['ol'],
      'lib/services': ['ol'],
      'lib/components': ['ol'],
      'lib/domain': ['ol'],
      lib: ['ol'],
    },

    'libs/friso/src/lib': ['friso'],
    'libs/shared/src/lib/i18n': ['i18n'],
    'libs/monitor/src/lib': ['monitor'],
    'libs/shared/src/lib/kpn/common': ['kpn/common'],
    'libs/shared/src/lib/kpn/filter': ['kpn/filter'],

    'libs/api/src/lib': {
      'common/common': ['api'],
      'common/tiles': ['api'],
      'common/changes/details': ['api'],
      'common/changes/filter': ['api'],
      'common/changes': ['api'],
      'common/route': ['api'],
      'common/network': ['api'],
      'common/node': ['api'],
      'common/statistics': ['api'],
      'common/planner': ['api'],
      'common/diff/common': ['api'],
      'common/diff/route': ['api'],
      'common/diff/network': ['api'],
      'common/diff/node': ['api'],
      'common/diff': ['api'],
      'common/status': ['api'],
      'common/subset': ['api'],
      'common/poi': ['api'],
      'common/data/raw': ['api'],
      'common/data': ['api'],
      'common/monitor': ['api'],
      'common/location': ['api'],
      common: ['api'],
      custom: ['api'],
    },
    'src/app': ['app'],
  },
  depRules: {
    root: ['*'],
    app: ['*'],
    api: ['api'],
    ol: [
      'ol',
      'api',
      'util',
      'components/shared', // depends on PageService
      'i18n', // layer name translations, replace with $localize locally
      'kpn/common', // depends on UniqueId, move to 'util' ?
      'services', // depends on PoiTileLayerService depends on PoiService
      'core', // MainMapStyleParameters and SurveyDateStyle depend on SurveyDateValues
    ],
    settings: ['core', 'components/shared'],
    'analysis/changes': [
      'api',
      'base',
      'ol',
      'core',
      'i18n',
      'util',
      'kpn/common',
      'kpn/filter',
      'services',
      'analysis/strategy',
      'analysis/components',
      'analysis/components/changes/filter',
      'components/shared',
      'components/shared/paginator',
      'components/shared/tags',
    ],
    'analysis/route': [
      'api',
      'base',
      'ol',
      'core',
      'i18n',
      'kpn/common',
      'services',
      'analysis/components',
      'analysis/fact',
      'components/shared',
      'components/shared/sidebar',
      'components/shared/tags',
    ],
    'analysis/network': [
      'api',
      'base',
      'ol',
      'core',
      'kpn/common',
      'kpn/filter',
      'services',
      'analysis/components',
      'analysis/components/edit',
      'analysis/fact',
      'components/shared',
      'components/shared/indicator',
      'components/shared/sidebar',
      'components/shared/tags',
    ],
    'analysis/overview': ['api', 'components/shared', 'kpn/common', 'services'],
    'analysis/node': [
      'api',
      'base',
      'ol',
      'core',
      'i18n',
      'kpn/common',
      'services',
      'analysis/components',
      'analysis/fact',
      'components/shared',
      'components/shared/sidebar',
      'components/shared/tags',
    ],
    'analysis/facts': [
      'api',
      'analysis/fact',
      'components/shared',
      'components/shared/sidebar',
    ],
    'analysis/analysis': [
      'api',
      'base',
      'ol',
      'core',
      'i18n',
      'util',
      'services',
      'kpn/common',
      'kpn/filter',
      'analysis/components',
      'analysis/components/edit',
      'analysis/fact',
      'analysis/strategy',
      'components/shared',
      'components/shared/indicator',
      'components/shared/paginator',
      'components/shared/sidebar',
      'components/shared/tags',
      'components/shared',
    ],
    'analysis/subset': [
      'api',
      'base',
      'ol',
      'core',
      'i18n',
      'kpn/common',
      'kpn/filter',
      'services',
      'analysis/components',
      'analysis/components/edit',
      'analysis/fact',
      'analysis/strategy',
      'components/shared',
    ],
    'analysis/strategy': ['core', 'components/shared'],
    'analysis/components/changes/route': [
      'api',
      'ol',
      'i18n',
      'components/shared',
      'components/shared/tags',
    ],
    'analysis/components/changes/node': [
      'api',
      'ol',
      'components/shared',
      'components/shared/tags',
    ],
    'analysis/components/changes': ['api', 'components/shared'],
    'analysis/components/changes/filter': [
      'api',
      'kpn/common',
      'components/shared',
    ],
    'analysis/components/indicators/route': ['api'],
    'analysis/components': [
      'ol',
      'components/shared',
      'analysis/fact',
      'analysis/strategy',
      'analysis/components/changes/route',
      'analysis/components/changes/node',
      'analysis/components/changes',
      'analysis/components/changes/filter',
      'analysis/components/indicators/route',
      'analysis/components/change-set',
      'analysis/components/change-set/components',
      'analysis/components/filter',
      'analysis/components/edit',
    ],
    'analysis/components/change-set': [
      'api',
      'i18n',
      'components/shared',
      'analysis/components/change-set/components',
    ],
    'analysis/components/change-set/components': ['api'],
    'analysis/components/filter': ['i18n', 'kpn/filter'],
    'analysis/components/edit': [
      'api',
      'core',
      'util',
      'services',
      'components/shared/paginator',
    ],
    'analysis/fact': ['api', 'components/shared'],
    'analysis/changeset': [
      'api',
      'ol',
      'core',
      'i18n',
      'util',
      'kpn/common',
      'kpn/filter',
      'services',
      'analysis/components',
      'components/shared',
      'components/shared/paginator',
      'components/shared/sidebar',
      'components/shared/tags',
    ],
    'analysis/location': [
      'api',
      'ol',
      'core',
      'util',
      'services',
      'kpn/common',
      'analysis/components',
      'analysis/components/edit',
      'analysis/fact',
      'analysis/strategy',
      'components/shared',
      'components/shared/indicator',
      'components/shared/paginator',
    ],
    planner: [
      'api',
      'base',
      'ol',
      'core',
      'i18n',
      'kpn/common',
      'util',
      'services',
      'components/poi',
      'components/shared',
      'components/shared/tags',
    ],
    status: ['api', 'components/shared', 'services'],
    base: ['api', 'core', 'kpn/common', 'spinner', 'components/shared'],
    spinner: ['core'],
    core: [
      'api', // "@api/common" in "shared/survey-date-values.ts"
      'analysis/components/edit', // EditParameters in shared.actions.ts and shared/shared.effects.ts
      'components/shared', //   in "preferences/preferences.reducer.ts"
      'components/shared/format', // DayPipe referenced in SurveyDateValues
      'core', // in "user/user.effects.ts"
      'services', // in "shared/shared.effects.ts" and "user/user.effects.ts"
    ],
    poi: [
      'api',
      'core',
      'base',
      'ol',
      'i18n',
      'kpn/common',
      'services',
      'util',
      'analysis/components',
      'analysis/location',
      'components/poi',
      'components/shared',
      'components/shared/paginator',
      'components/shared/tags',
    ],
    demo: ['core', 'components/shared'],
    services: ['api', 'ol', 'core'],
    util: [],
    'components/shared/tags': ['api', 'kpn/common'],
    'components/shared/json': [],
    'components/shared/link': ['api', 'core', 'i18n', 'services'],
    'components/shared': [
      'api',
      'i18n',
      'services',
      'spinner',
      'components/shared/data',
      'components/shared/day',
      'components/shared/dialog',
      'components/shared/error',
      'components/shared/format',
      'components/shared/icon',
      'components/shared/indicator',
      'components/shared/items',
      'components/shared/json',
      'components/shared/link',
      'components/shared/menu',
      'components/shared/page',
      'components/shared/paginator',
      'components/shared/sidebar',
      'components/shared/tags',
      'components/shared/timestamp',
      'components/shared/toolbar',
    ],
    'components/shared/items': [],
    'components/shared/day': ['api'],
    'components/shared/dialog': [],
    'components/shared/paginator': [],
    'components/shared/toolbar': [
      'api',
      'i18n',
      'kpn/common',
      'services',
      'spinner',
      'components/shared',
    ],
    'components/shared/icon': [],
    'components/shared/indicator': ['api', 'kpn/common'],
    'components/shared/error': ['core'],
    'components/shared/data': [],
    'components/shared/page': ['i18n', 'services'],
    'components/shared/format': [
      'api',
      'core',
      'i18n',
      'kpn/common',
      'services',
      'spinner',
      'components/shared',
    ],
    'components/shared/timestamp': ['api'],
    'components/shared/sidebar': [
      'api',
      'core',
      'i18n',
      'kpn/common',
      'services',
      'spinner',
      'components/shared',
    ],
    'components/shared/menu': [],
    'components/poi': [
      'api',
      'core',
      'i18n',
      'kpn/common',
      'services',
      'spinner',
      'components/shared',
    ],
    friso: ['api', 'ol', 'core', 'components/shared'],
    i18n: [],
    monitor: [
      'api',
      'ol',
      'core',
      'util',
      'analysis/components/edit', // EditParameters?
      'components/shared',
      'components/shared/sidebar',
    ],
    'kpn/common': ['api'],
    'kpn/filter': ['api', 'kpn/common'],
  },
};
