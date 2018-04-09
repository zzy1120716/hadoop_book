import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class FileSystemGlobTest {

    private static final String BASE_PATH = "/tmp/" +
            FileSystemGlobTest.class.getSimpleName();

    private FileSystem fs;

    @Before
    public void setUp() throws Exception {
        fs = FileSystem.get(new Configuration());
        fs.mkdirs(new Path(BASE_PATH, "2017/12/30"));
        fs.mkdirs(new Path(BASE_PATH, "2017/12/31"));
        fs.mkdirs(new Path(BASE_PATH, "2018/01/01"));
        fs.mkdirs(new Path(BASE_PATH, "2018/01/02"));
    }

    @After
    public void tearDown() throws Exception {
        fs.delete(new Path(BASE_PATH), true);
    }

    @Test
    public void glob() throws Exception {
        assertThat(glob("/*"), is(paths("/2017", "/2018")));
        assertThat(glob("/*/*"), is(paths("/2017/12", "/2018/01")));
        assertThat(glob("/*/12/*"), is(paths("/2017/12/30", "/2017/12/31")));
        assertThat(glob("/201?"), is(paths("/2017", "/2018")));
        assertThat(glob("/201[78]"), is(paths("/2017", "/2018")));
        assertThat(glob("/201[7-8]"), is(paths("/2017", "/2018")));
        assertThat(glob("/201[^01234569]"), is(paths("/2017", "/2018")));

        assertThat(glob("/*/*/{31,01}"), is(paths("/2017/12/31", "/2018/01/01")));
        assertThat(glob("/*/*/3{0,1}"), is(paths("/2017/12/30", "/2017/12/31")));

        assertThat(glob("/*/{12/31,01/01}"), is(paths("/2017/12/31", "/2018/01/01")));
    }

    @Test
    public void regexIncludes() throws Exception {
        assertThat(glob("/*", new RegexPathFilter("^.*/2017$")), is(paths("/2017")));
        assertThat(glob("/*/*/*", new RegexPathFilter("^.*/2017/12/31$")), is(paths("/2017/12/31")));
        assertThat(glob("/*/*/*", new RegexPathFilter("^.*/2017(/12(/31)?)?$")), is(paths("/2017/12/31")));
    }

    @Test
    public void regexExcludes() throws Exception {
        assertThat(glob("/*", new RegexPathFilter("^.*/2017$", false)), is(paths("/2018")));
        assertThat(glob("/2017/*/*", new RegexPathFilter("^.*/2017/12/31$", false)), is(paths("/2017/12/30")));
    }

    @Test
    public void regexExcludesWithRegexExcludePathFilter() throws Exception {
        assertThat(glob("/*", new RegexExcludePathFilter("^.*/2017$")), is(paths("/2018")));
        assertThat(glob("/2017/*/*", new RegexExcludePathFilter("^.*/2017/12/31$")), is(paths("/2017/12/30")));
    }

    @Test
    public void testDateRange() throws Exception {
        DateRangePathFilter filter = new DateRangePathFilter(date("2017/12/31"), date("2018/01/01"));
        assertThat(glob("/*/*/*", filter), is(paths("/2017/12/31", "/2018/01/01")));
    }

    //匹配文件路径，返回Path集合
    private Set<Path> glob(String pattern) throws IOException {
        return new HashSet<Path>(Arrays.asList(
                FileUtil.stat2Paths(fs.globStatus(new Path(BASE_PATH + pattern)))
            ));
    }

    //匹配文件路径（带过滤器），返回Path集合
    private Set<Path> glob(String pattern, PathFilter pathFilter) throws IOException {
        return new HashSet<Path>(Arrays.asList(
                FileUtil.stat2Paths(fs.globStatus(new Path(BASE_PATH + pattern), pathFilter))
            ));
    }

    //不定参字符串或字符串数组，转化为Path集合
    private Set<Path> paths(String... pathStrings) {
        Path[] paths = new Path[pathStrings.length];
        for(int i = 0; i < paths.length; i++) {
            paths[i] = new Path("file:" + BASE_PATH + pathStrings[i]);
        }
        return new HashSet<Path>(Arrays.asList(paths));
    }

    //字符串日期转为Date日期
    private Date date(String date) throws ParseException {
        return new SimpleDateFormat("yyyy/MM/dd").parse(date);
    }
}
