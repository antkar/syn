#!/bin/bash

D="$(dirname $0)"
S="$D/sample"

java -jar "$D/target/sample-script-1.2.jar" "$S/Utils.syns" "$S/Snake.syns" "$S/Level.syns" "$S/World.syns" "$S/View.syns"

