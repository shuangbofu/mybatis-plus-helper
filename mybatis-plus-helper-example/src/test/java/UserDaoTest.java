/**
 * Created by shuangbofu on 2021/8/22 1:56 下午
 */

import com.example.App;
import com.example.dao.UserDao;
import com.example.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest(classes = App.class)
@RunWith(SpringRunner.class)
public class UserDaoTest {

    @Autowired
    private UserDao userDao;

    @Test
    public void test() {
        List<User> users = userDao.selectAll();
        List<String> names = userDao.selectValueListBy(User::getName, i -> i.lambda().like(User::getName, "%john%"));
        System.out.println(names);
    }
}
