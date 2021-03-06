package meta;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.star.meta.TimeAndSequences;
import com.star.meta.TimeStampAndSequence;
import io.netty.util.internal.ConcurrentSet;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.*;

public class TestTimeAndSequences {

    @Test
    public void init() {
        Module module = new TestModule();
        Injector injector = Guice.createInjector(module);
        TimeAndSequences timeAndSequences = injector.getInstance(TimeAndSequences.class);
        Set<TimeStampAndSequence> set = new ConcurrentSet<>();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<Future> futures = new ArrayList<>(1000);
        Runnable runnable = () -> {
            for (int i = 0; i < 10000; i++) {
                TimeStampAndSequence calculate = timeAndSequences.calculate();
                if (!set.add(calculate)) {
                     throw new RuntimeException(calculate.toString());
//                    System.out.println(calculate + " is dup");

                }

            }
        };
        long currentTimeMillis = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            futures.add(executorService.submit(runnable));
        }
        futures.forEach(future -> {
            try {
                future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        System.out.println(System.currentTimeMillis() - currentTimeMillis);
        System.out.println(System.currentTimeMillis());
    }
}
