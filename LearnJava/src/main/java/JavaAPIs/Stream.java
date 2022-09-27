package JavaAPIs;

import java.util.*;
import java.util.stream.Collectors;

/**
 * :Description:Java8 Stream
 * :Author: 佳境Shmily
 * :Create Time: 2022/3/13 15:59
 * :Site: shmily-qjj.top
 * Java 8 API添加了一个新的抽象称为流Stream，可以让你以一种声明的方式处理数据。
 *
 * Stream 使用一种类似用 SQL 语句从数据库查询数据的直观方式来提供一种对 Java 集合运算和表达的高阶抽象。
 *
 * Stream API可以极大提高Java程序员的生产力，让程序员写出高效率、干净、简洁的代码。
 *
 * 这种风格将要处理的元素集合看作一种流， 流在管道中传输， 并且可以在管道的节点上进行处理， 比如筛选， 排序，聚合等。
 *
 * 元素流在管道中经过中间操作（intermediate operation）的处理，最后由最终操作(terminal operation)得到前面处理的结果。
 *
 * Stream（流）是一个来自数据源的元素队列并支持聚合操作
 *
 * 元素是特定类型的对象，形成一个队列。 Java中的Stream并不会存储元素，而是按需计算。
 * 数据源 流的来源。 可以是集合，数组，I/O channel， 产生器generator 等。
 * 聚合操作 类似SQL语句一样的操作， 比如filter, map, reduce, find, match, sorted等。
 * 和以前的Collection操作不同， Stream操作还有两个基础的特征：
 *
 * Pipelining: 中间操作都会返回流对象本身。 这样多个操作可以串联成一个管道， 如同流式风格（fluent style）。 这样做可以对操作进行优化， 比如延迟执行(laziness)和短路( short-circuiting)。
 * 内部迭代： 以前对集合遍历都是通过Iterator或者For-Each的方式, 显式的在集合外部进行迭代， 这叫做外部迭代。 Stream提供了内部迭代的方式， 通过访问者模式(Visitor)实现。
 *
 * +--------------------+       +------+   +------+   +---+   +-------+
 * | stream of elements +-----> |filter+-> |sorted+-> |map+-> |collect|
 * +--------------------+       +------+   +------+   +---+   +-------+
 *以上的流程转换为 Java 代码为：
 *
 * List<Integer> transactionsIds =
 * widgets.stream()
 *              .filter(b -> b.getColor() == RED)
 *              .sorted((x,y) -> x.getWeight() - y.getWeight())
 *              .mapToInt(Widget::getWeight)
 *              .sum();
 *
 * 串行流：适合存在线程安全问题、阻塞任务、重量级任务，以及需要使用同一事务的逻辑。
 *
 * 并行流：适合没有线程安全问题、较单纯的数据处理任务。
 */

public class Stream {
    public static void main(String[] args) {
        List<String> strings = Arrays.asList("abc", "", "bc", "efg", "abcd","", "jkl");
        // filter
        List<String> filtered = strings.stream().filter(str -> !str.isEmpty()).collect(Collectors.toList());
        System.out.println(filtered);
        System.out.println(strings.stream().filter(String::isEmpty).count());
        System.out.println("======================================");

        //forEach
        Random random = new Random();
        random.ints(3,1,10).forEach(System.out::println);
        System.out.println("======================================");

        // map
        random.ints(3,1,10).map(i -> i*i).forEach(System.out::println);
        System.out.println("======================================");

        // limit
        System.out.println(random.ints(3, 1, 10).count());
        System.out.println("======================================");

        // sorted
        Arrays.stream(new int[]{6, 4, 2, 5, 6, 8, 9}).sorted().forEach(System.out::println);
        System.out.println("======================================");

        // reduce
        System.out.println(Arrays.stream(new int[]{6, 4, 2, 5, 6, 8, 9}).reduce(0, Integer::sum));
        System.out.println(Arrays.stream(new int[]{6, 4, 2, 5, 6, 8, 9}).reduce(Integer::sum));
        System.out.println(Arrays.stream(new int[]{6, 4, 2, 5, 6, 8, 9}).reduce(Integer::min).getAsInt());
        System.out.println(Arrays.stream(new int[]{6, 4, 2, 5, 6, 8, 9}).reduce((i,j) -> i+j).getAsInt());
        System.out.println(Arrays.stream(new int[]{6, 4, 2, 5, 6, 8, 9}).reduce((i,j) -> i > j ? j : i));
        System.out.println("======================================");

        // parallel 并行执行  多线程  指定线程数-Djava.util.concurrent.ForkJoinPool.common.parallelism=N
        // 由于并行流使用多线程，则一切线程安全问题都应该是需要考虑的问题，如：资源竞争、死锁、事务、可见性等等。
        Arrays.asList(6, 4, 2, 5, 6, 8, 9).parallelStream().sorted().forEach(num -> System.out.println(Thread.currentThread().getName() + " >> " + num));
        System.out.println("======================================");

        // Collectors Collectors 类实现了很多归约操作，例如将流转换成集合和聚合元素。
        List<String> collect = strings.stream().filter(x -> !x.isEmpty()).collect(Collectors.toList());
        collect.forEach(System.out::println);
        System.out.println("======================================");

        // 统计
        IntSummaryStatistics summary = java.util.stream.Stream.of(3, 2, 2, 3, 7, 3, 5).mapToInt((x) -> x).summaryStatistics();
        System.out.println(String.format("AVG:%s Count:%s Sum:%s Max:%s Min:%s", summary.getAverage(), summary.getCount(), summary.getSum(), summary.getMax(),summary.getMin()));

        System.out.println("##########################################################");
        // 批量修改过滤和修改Map中的元素（包括过滤value、修改key）
        final Map<String,String> map =  new HashMap<String, String>(10){
            {
                put("p__aa", "value1");
                put("p__bb", "value2");
                put("p__cc", "value3");
                put("p_dd", "value4");
                put("p_ee", "value5");
                put("p_ff", "value6");
                put("date", "value7");
                put("nullVal1", null);
                put("nullVal2", null);
            }
        };
        // 需求： 批量将map的key中 p__x变为$_x，p_x变为x，value为null的去掉
        //        HashMap<String,String> out = new HashMap<>(map.size());
        //        for (Map.Entry<String,String> e:map.entrySet()) {
        //            if(e.getValue() == null )
        //                continue;
        //            out.put(e.getKey().replace("p__","$_").replace("p_",""), e.getValue());
        //        }
        //        out.forEach((key, value) -> System.out.println(key + "---" + value));

        Map<String, String> collect1 = map.entrySet().stream().filter(e -> e.getValue() != null).collect(Collectors.toMap(e -> (e.getKey().replace("p__", "$_").replace("p_", "")), Map.Entry::getValue));
        collect1.forEach((k, v) -> System.out.println(k + "===" + v));

        System.out.println("##########################################################");

        // value为Object类型时处理   Map<String,Object> 转换为 Map<String,String>
        final Map<String,Object> map1 =  new HashMap<String, Object>(10){
            {
                put("p__aa", "value1");
                put("p__bb", 12);
                put("p__cc", true);
                put("p_dd", "value2");
                put("p_ee", "value3");
                put("p_ff", 4);
                put("date", 5);
                put("nullVal1", null);
                put("nullVal2", null);
            }
        };
        Map<String, String> collect2 = map1.entrySet().parallelStream().filter(e -> e.getValue() != null).collect(Collectors.toMap(e -> (e.getKey().replace("p__", "$")), e -> (String.valueOf(e.getValue()))));
        collect2.forEach((k,v) -> System.out.println(k + "==" + v));
    }
}
