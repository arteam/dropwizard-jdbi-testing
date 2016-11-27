package com.github.arteam.jdbi3;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.github.arteam.jdbi3.strategies.NameStrategies;
import com.github.arteam.jdbi3.strategies.ShortNameStrategy;
import com.github.arteam.jdbi3.strategies.SmartNameStrategy;
import com.github.arteam.jdbi3.strategies.StatementNameStrategy;
import org.jdbi.v3.core.ExtensionMethod;
import org.jdbi.v3.core.StatementContext;
import org.junit.Test;
import org.mockito.Mockito;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import static com.codahale.metrics.MetricRegistry.name;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class InstrumentedTimingCollectorTest {
    private final MetricRegistry registry = new MetricRegistry();

    @Test
    public void updatesTimerForSqlObjects() throws Exception {
        final StatementNameStrategy strategy = new SmartNameStrategy();
        final InstrumentedTimingCollector collector = new InstrumentedTimingCollector(registry,
                strategy);
        final StatementContext ctx = mock(StatementContext.class);
        doReturn("SELECT 1").when(ctx).getRawSql();
        doReturn(new ExtensionMethod(getClass(), getClass().getMethod("updatesTimerForSqlObjects")))
                .when(ctx).getExtensionMethod();

        collector.collect(TimeUnit.SECONDS.toNanos(1), ctx);

        final String name = strategy.getStatementName(ctx);
        final Timer timer = registry.timer(name);

        assertThat(name)
                .isEqualTo(name(getClass(), "updatesTimerForSqlObjects"));
        assertThat(timer.getSnapshot().getMax())
                .isEqualTo(1000000000);
    }

    @Test
    public void updatesTimerForRawSql() throws Exception {
        final StatementNameStrategy strategy = new SmartNameStrategy();
        final InstrumentedTimingCollector collector = new InstrumentedTimingCollector(registry,
                strategy);
        final StatementContext ctx = mock(StatementContext.class);
        doReturn("SELECT 1").when(ctx).getRawSql();

        collector.collect(TimeUnit.SECONDS.toNanos(2), ctx);

        final String name = strategy.getStatementName(ctx);
        final Timer timer = registry.timer(name);

        assertThat(name)
                .isEqualTo(name("sql", "raw", "SELECT 1"));
        assertThat(timer.getSnapshot().getMax())
                .isEqualTo(2000000000);
    }

    @Test
    public void updatesTimerForNoRawSql() throws Exception {
        final StatementNameStrategy strategy = new SmartNameStrategy();
        final InstrumentedTimingCollector collector = new InstrumentedTimingCollector(registry,
                strategy);
        final StatementContext ctx = mock(StatementContext.class);

        collector.collect(TimeUnit.SECONDS.toNanos(2), ctx);

        final String name = strategy.getStatementName(ctx);
        final Timer timer = registry.timer(name);

        assertThat(name)
                .isEqualTo(name("sql", "empty"));
        assertThat(timer.getSnapshot().getMax())
                .isEqualTo(2000000000);
    }

    @Test
    public void updatesTimerForNonSqlishRawSql() throws Exception {
        final StatementNameStrategy strategy = new SmartNameStrategy();
        final InstrumentedTimingCollector collector = new InstrumentedTimingCollector(registry,
                strategy);
        final StatementContext ctx = mock(StatementContext.class);
        doReturn("don't know what it is but it's not SQL").when(ctx).getRawSql();

        collector.collect(TimeUnit.SECONDS.toNanos(3), ctx);

        final String name = strategy.getStatementName(ctx);
        final Timer timer = registry.timer(name);

        assertThat(name)
                .isEqualTo(name("sql", "raw", "don't know what it is but it's not SQL"));
        assertThat(timer.getSnapshot().getMax())
                .isEqualTo(3000000000L);
    }

    @Test
    public void updatesTimerForContextClass() throws Exception {
        final StatementNameStrategy strategy = new SmartNameStrategy();
        final InstrumentedTimingCollector collector = new InstrumentedTimingCollector(registry,
                strategy);
        final StatementContext ctx = mock(StatementContext.class);
        doReturn("SELECT 1").when(ctx).getRawSql();
        doReturn(getClass().getName()).when(ctx).getAttribute(NameStrategies.STATEMENT_CLASS);
        doReturn("updatesTimerForContextClass").when(ctx)
                .getAttribute(NameStrategies.STATEMENT_NAME);

        collector.collect(TimeUnit.SECONDS.toNanos(3), ctx);

        final String name = strategy.getStatementName(ctx);
        final Timer timer = registry.timer(name);

        assertThat(name)
                .isEqualTo(name(getClass(), "updatesTimerForContextClass"));
        assertThat(timer.getSnapshot().getMax())
                .isEqualTo(3000000000L);
    }

    @Test
    public void updatesTimerForTemplateFile() throws Exception {
        final StatementNameStrategy strategy = new SmartNameStrategy();
        final InstrumentedTimingCollector collector = new InstrumentedTimingCollector(registry,
                strategy);
        final StatementContext ctx = mock(StatementContext.class);
        doReturn("SELECT 1").when(ctx).getRawSql();
        doReturn("foo/bar.stg").when(ctx).getAttribute(NameStrategies.STATEMENT_GROUP);
        doReturn("updatesTimerForTemplateFile").when(ctx)
                .getAttribute(NameStrategies.STATEMENT_NAME);

        collector.collect(TimeUnit.SECONDS.toNanos(4), ctx);

        final String name = strategy.getStatementName(ctx);
        final Timer timer = registry.timer(name);

        assertThat(name)
                .isEqualTo(name("foo", "bar", "updatesTimerForTemplateFile"));
        assertThat(timer.getSnapshot().getMax())
                .isEqualTo(4000000000L);
    }

    @Test
    public void updatesTimerForContextGroupAndName() throws Exception {
        final StatementNameStrategy strategy = new SmartNameStrategy();
        final InstrumentedTimingCollector collector = new InstrumentedTimingCollector(registry,
                strategy);
        final StatementContext ctx = mock(StatementContext.class);
        doReturn("SELECT 1").when(ctx).getRawSql();
        doReturn("my-group").when(ctx).getAttribute(NameStrategies.STATEMENT_GROUP);
        doReturn("updatesTimerForContextGroupAndName").when(ctx)
                .getAttribute(NameStrategies.STATEMENT_NAME);

        collector.collect(TimeUnit.SECONDS.toNanos(4), ctx);

        final String name = strategy.getStatementName(ctx);
        final Timer timer = registry.timer(name);

        assertThat(name)
                .isEqualTo(name("my-group", "updatesTimerForContextGroupAndName", ""));
        assertThat(timer.getSnapshot().getMax())
                .isEqualTo(4000000000L);
    }

    @Test
    public void updatesTimerForContextGroupTypeAndName() throws Exception {
        final StatementNameStrategy strategy = new SmartNameStrategy();
        final InstrumentedTimingCollector collector = new InstrumentedTimingCollector(registry,
                strategy);
        final StatementContext ctx = mock(StatementContext.class);
        doReturn("SELECT 1").when(ctx).getRawSql();
        doReturn("my-group").when(ctx).getAttribute(NameStrategies.STATEMENT_GROUP);
        doReturn("my-type").when(ctx).getAttribute(NameStrategies.STATEMENT_TYPE);
        doReturn("updatesTimerForContextGroupTypeAndName").when(ctx)
                .getAttribute(NameStrategies.STATEMENT_NAME);

        collector.collect(TimeUnit.SECONDS.toNanos(5), ctx);

        final String name = strategy.getStatementName(ctx);
        final Timer timer = registry.timer(name);

        assertThat(name)
                .isEqualTo(name("my-group", "my-type", "updatesTimerForContextGroupTypeAndName"));
        assertThat(timer.getSnapshot().getMax())
                .isEqualTo(5000000000L);
    }

    @Test
    public void updatesTimerForShortSqlObjectStrategy() throws Exception {
        final StatementNameStrategy strategy = new ShortNameStrategy("jdbi");
        final InstrumentedTimingCollector collector = new InstrumentedTimingCollector(registry,
                strategy);
        final StatementContext ctx = mock(StatementContext.class);
        doReturn("SELECT 1").when(ctx).getRawSql();
        doReturn(new ExtensionMethod(getClass(), getClass().getMethod("updatesTimerForShortSqlObjectStrategy")))
                .when(ctx).getExtensionMethod();

        collector.collect(TimeUnit.SECONDS.toNanos(1), ctx);

        final String name = strategy.getStatementName(ctx);
        final Timer timer = registry.timer(name);

        assertThat(name)
                .isEqualTo(name("jdbi",
                        getClass().getSimpleName(),
                        "updatesTimerForShortSqlObjectStrategy"));
        assertThat(timer.getSnapshot().getMax())
                .isEqualTo(1000000000);
    }

    @Test
    public void updatesTimerForShortContextClassStrategy() throws Exception {
        final StatementNameStrategy strategy = new ShortNameStrategy("jdbi");
        final InstrumentedTimingCollector collector = new InstrumentedTimingCollector(registry,
                strategy);
        final StatementContext ctx = mock(StatementContext.class);
        doReturn("SELECT 1").when(ctx).getRawSql();
        doReturn(getClass().getName()).when(ctx).getAttribute(NameStrategies.STATEMENT_CLASS);
        doReturn("updatesTimerForShortContextClassStrategy").when(ctx)
                .getAttribute(NameStrategies.STATEMENT_NAME);

        collector.collect(TimeUnit.SECONDS.toNanos(3), ctx);

        final String name = strategy.getStatementName(ctx);
        final Timer timer = registry.timer(name);

        assertThat(name)
                .isEqualTo(name("jdbi",
                        getClass().getSimpleName(),
                        "updatesTimerForShortContextClassStrategy"));
        assertThat(timer.getSnapshot().getMax())
                .isEqualTo(3000000000L);
    }
}
