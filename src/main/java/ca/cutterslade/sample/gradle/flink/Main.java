/*
 * Copyright 2017 Wesley Hartford
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.cutterslade.sample.gradle.flink;

import java.util.Arrays;

import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.util.Collector;

public final class Main {
  public static void main(String[] args) throws Exception {
    final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
    env.readTextFile(args[0])
        .map(line -> line.toLowerCase().split("\\W+"))
        .flatMap(
            (String[] words, Collector<Tuple2<String, Long>> out) -> Arrays.stream(words)
                .filter(word -> !word.isEmpty())
                .forEach(word -> out.collect(new Tuple2<>(word, 1L)))
        )
        .groupBy(0).sum(1)
        .writeAsCsv(args[0] + ".out", FileSystem.WriteMode.OVERWRITE);
    env.execute(Main.class.getName());
  }
}
