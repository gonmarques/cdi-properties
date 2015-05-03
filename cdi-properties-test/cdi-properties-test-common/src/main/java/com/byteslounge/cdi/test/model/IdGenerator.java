package com.byteslounge.cdi.test.model;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.SequenceGenerator;

public class IdGenerator extends SequenceGenerator {

    @Override
    public Serializable generate(SessionImplementor session, Object obj) throws HibernateException {
        if (obj == null) {
            throw new HibernateException(new IllegalArgumentException("obj may not be null"));
        }
        Object id = ((TestEntity) obj).getId();
        if (id == null) {
            return super.generate(session, obj);
        }
        return (Serializable) id;
    }

}
