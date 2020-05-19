package ru.javaops.masterjava.persist.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.PreparedBatch;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import org.slf4j.Logger;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.model.User;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class UserDao implements AbstractDao {

    private static final Logger log = getLogger(UserDao.class);

    public User insert(User user) {
        if (user.isNew()) {
            int id = insertGeneratedId(user);
            user.setId(id);
        } else {
            insertWitId(user);
        }
        return user;
    }

    @SqlUpdate("INSERT INTO users (full_name, email, flag) VALUES (:fullName, :email, CAST(:flag AS user_flag)) ")
    @GetGeneratedKeys
    abstract int insertGeneratedId(@BindBean User user);

    @SqlUpdate("INSERT INTO users (id, full_name, email, flag) VALUES (:id, :fullName, :email, CAST(:flag AS user_flag)) " +
            "ON CONFLICT (email) DO NOTHING ")
    abstract void insertWitId(@BindBean User user);

    @SqlQuery("SELECT * FROM users ORDER BY full_name, email LIMIT :it")
    public abstract List<User> getWithLimit(@Bind int limit);

    //   http://stackoverflow.com/questions/13223820/postgresql-delete-all-content
    @SqlUpdate("TRUNCATE users")
    @Override
    public abstract void clean();

    public int insertBatchChunkNumber(List<User> userList, int chunkNumber) {
        Handle handle = DBIProvider.getDBI().open();
        PreparedBatch batch = handle.prepareBatch("INSERT INTO users (full_name, email, flag) VALUES (:fullName, :email, " +
                "CAST(:flag AS user_flag)) ON CONFLICT (email) DO NOTHING");
        int j = 0;
        int result = 0;
        try {
            for (User user : userList) {
                batch.bind("fullName", user.getFullName()).bind("email", user.getEmail())
                        .bind("flag", user.getFlag()).add();
                j++;
                if (j == userList.size() || j == chunkNumber) {
                    result = batch.execute().length;
                    j = 0;
                }
            }
            if (batch.getSize() != 0) {
                result += batch.execute().length;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            handle.close();
        }
        return result;
    }

    @SqlQuery("SELECT * FROM users ORDER BY full_name LIMIT 20")
    public abstract List<User> getLimit();
}
