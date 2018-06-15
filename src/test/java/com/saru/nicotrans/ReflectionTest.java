package com.saru.nicotrans;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.lang.reflect.Method;
import java.util.Enumeration;

import static org.assertj.core.api.Assertions.assertThat;

public class ReflectionTest {
    private static String createSetMethod(String fieldName) {
        return "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    @Test
    public void javabean() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("name", "bbb");
        request.addParameter("email", "aaa@ccc.net");
        request.addParameter("age", "11");

        Class<Person> personClass = Person.class;
        Object person = personClass.newInstance();
        Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            try {
                Method method = personClass.getMethod(createSetMethod(name), String.class);
                method.invoke(person, request.getParameter(name));
            } catch (NoSuchMethodException e) {
            }
        }
        System.out.println("Person : " + person);

        Class<PersonController> personControllerClass = PersonController.class;
        Method method = personControllerClass.getMethod("add", person.getClass());
        method.invoke(personControllerClass.newInstance(), person);
    }

    @Test
    public void setMethod() {
        assertThat(createSetMethod("title")).isEqualTo("setTitle");
    }

    public static class Person {
        private String name;

        private String email;

        public String getName() {
            return name;
        }

        public Person setName(String name) {
            this.name = name;
            return this;
        }

        public String getEmail() {
            return email;
        }

        public Person setEmail(String email) {
            this.email = email;
            return this;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "name='" + name + '\'' +
                    ", email='" + email + '\'' +
                    '}';
        }
    }

    static class PersonController {
        String add(Person person) {
            System.out.println("Controller Person : " + person);
            return "/persons";
        }
    }
}