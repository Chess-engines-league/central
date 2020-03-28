package net.purevirtual.chell.central.web.crud.control;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.purevirtual.chell.central.web.crud.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class UserManager {

    private static final Logger logger = LoggerFactory.getLogger(UserManager.class);

    @PersistenceContext
    private EntityManager entityManager;

    public User get(Integer id) {
        return entityManager.find(User.class, id);
    }

    public User getByLogin(String login) {
        return entityManager.createQuery("select u from User u where u.login=:login", User.class)
                .setParameter("login", login)
                .getSingleResult();
    }

    public void create(String login) {
        entityManager.createNativeQuery("insert into \"user\" (login) values(:login) on conflict DO NOTHING")
                .setParameter("login", login)
                .executeUpdate();
    }

}
