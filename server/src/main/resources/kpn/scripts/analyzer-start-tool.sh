#!/usr/bin/env bash
nohup /kpn/java/bin/java \
  -Dlog4j.configurationFile=/kpn/conf/analyzer-start-tool-log.xml \
  -cp /kpn/app/lib/core.core-*.jar:/kpn/app/lib/* \
  kpn.core.tools.AnalyzerStartTool -c changes2 -a master3 > /kpn/logs/analyzer-start-tool-stdout.log 2>&1 &
