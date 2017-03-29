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
