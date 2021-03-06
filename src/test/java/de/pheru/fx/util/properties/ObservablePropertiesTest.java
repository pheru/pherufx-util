package de.pheru.fx.util.properties;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.util.StringConverter;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import static org.junit.Assert.*;

public class ObservablePropertiesTest {

    private static final String DEFAULT_STRING = "defaultValue";
    private static final boolean DEFAULT_BOOLEAN = false;
    private static final int DEFAULT_INTEGER = 9999;
    private static final long DEFAULT_LONG = 999999L;
    private static final float DEFAULT_FLOAT = 99.99F;
    private static final double DEFAULT_DOUBLE = 999.99;
    private static TestObject DEFAULT_TESTOBJECT = new TestObject(99, "NeunUndNeunzig");

    private ObservableProperties observableProperties;

    @Before
    public void setUp() throws Exception {
        observableProperties = new ObservableProperties();
        observableProperties.load(new File("src/test/resources/properties/testproperties.foo").getAbsolutePath());
    }

    @Test
    public void saveNew() throws Exception {
        final File file = new File("src/test/resources/properties/savetestnew.bar");
        if (file.exists()) {
            if (!file.delete()) {
                fail("File already exists and could not be deleted");
            }
        }
        observableProperties = new ObservableProperties();
        observableProperties.stringProperty("newString", "new");
        observableProperties.integerProperty("newInteger", 123);
        observableProperties.longProperty("newLong", 12345L);
        observableProperties.floatProperty("newFloat", 123.45F);
        observableProperties.doubleProperty("newDouble", 12345.67);
        observableProperties.booleanProperty("newBoolean", true);
        observableProperties.save("comment", file.getAbsolutePath());

        final Properties savedProperties = new Properties();
        try (final FileInputStream inputStream = new FileInputStream(file)) {
            savedProperties.load(inputStream);
        }
        assertEquals("new", savedProperties.getProperty("newString"));
        assertEquals("12345", savedProperties.getProperty("newLong"));

        if (!file.delete()) {
            fail("Failed to delete file!");
        }
    }

    @Test
    public void saveExisting() throws Exception {
        final File newFile = new File("src/test/resources/properties/testproperties.fooNEW");
        if (newFile.exists()) {
            if (!newFile.delete()) {
                fail("New file already exists and could not be deleted");
            }
        }

        final StringProperty stringProperty = observableProperties.stringProperty("stringKey", DEFAULT_STRING);
        stringProperty.set("newValue");
        final DoubleProperty doubleProperty = observableProperties.doubleProperty("doubleKey", DEFAULT_DOUBLE);
        doubleProperty.set(99.99);
        observableProperties.save("comment", newFile.getAbsolutePath());

        final Properties savedProperties = new Properties();
        try (final FileInputStream inputStream = new FileInputStream(newFile)) {
            savedProperties.load(inputStream);
        }
        assertEquals("1234", savedProperties.getProperty("integerKey"));
        assertEquals("newValue", savedProperties.getProperty("stringKey"));
        assertEquals("99.99", savedProperties.getProperty("doubleKey"));

        if (!newFile.delete()) {
            fail("Failed to delete file!");
        }
    }

    @Test
    public void saveNoFilePath() throws Exception {
        try {
            new ObservableProperties().save(null);
            fail("Exception expected!");
        } catch (final IllegalStateException e) {
            assertTrue(e.getMessage().endsWith(ObservableProperties.NO_FILEPATH_EXCEPTION_MSG));
        }
    }

    @Test
    public void contains() throws Exception {
        assertTrue(observableProperties.contains("booleanKey"));
        assertTrue(observableProperties.contains(new ObservablePropertyKey<>("booleanKey", DEFAULT_BOOLEAN)));
        assertFalse(observableProperties.contains("notExistingKey"));
        assertFalse(observableProperties.contains(new ObservablePropertyKey<>("notExistingKey", DEFAULT_BOOLEAN)));
    }

    @Test
    public void loadedTwice() throws Exception {
        assertTrue(observableProperties.contains("booleanKey"));
        observableProperties.load(new File("src/test/resources/properties/emptyproperties.foo").getAbsolutePath());
        assertFalse(observableProperties.contains("booleanKey"));
    }

    @Test
    public void stringProperty() throws Exception {
        assertEquals("stringValue", observableProperties.stringProperty("stringKey", DEFAULT_STRING).get());
        assertEquals("", observableProperties.stringProperty("stringKeyEmpty", DEFAULT_STRING).get());
        assertEquals(DEFAULT_STRING, observableProperties.stringProperty("stringKeyNull", DEFAULT_STRING).get());
    }

    @Test
    public void booleanProperty() throws Exception {
        assertEquals(true, observableProperties.booleanProperty("booleanKey", DEFAULT_BOOLEAN).get());
        assertEquals(true, observableProperties.booleanProperty(new ObservablePropertyKey<>("booleanKey", DEFAULT_BOOLEAN)).get());
        assertEquals(true, observableProperties.booleanProperty("booleanKey", DEFAULT_BOOLEAN).get());
        assertEquals(DEFAULT_BOOLEAN, observableProperties.booleanProperty("booleanKeyEmpty", DEFAULT_BOOLEAN).get());
        assertEquals(DEFAULT_BOOLEAN, observableProperties.booleanProperty("booleanKeyNull", DEFAULT_BOOLEAN).get());
    }

    @Test
    public void integerProperty() throws Exception {
        assertEquals(1234, observableProperties.integerProperty("integerKey", DEFAULT_INTEGER).get());
        assertEquals(DEFAULT_INTEGER, observableProperties.integerProperty("integerKeyEmpty", DEFAULT_INTEGER).get());
        assertEquals(DEFAULT_INTEGER, observableProperties.integerProperty("integerKeyInvalid", DEFAULT_INTEGER).get());
        assertEquals(DEFAULT_INTEGER, observableProperties.integerProperty("integerKeyNull", DEFAULT_INTEGER).get());
    }

    @Test
    public void longProperty() throws Exception {
        assertEquals(123456, observableProperties.longProperty("longKey", DEFAULT_LONG).get());
        assertEquals(DEFAULT_LONG, observableProperties.longProperty("longKeyEmpty", DEFAULT_LONG).get());
        assertEquals(DEFAULT_LONG, observableProperties.longProperty("longKeyInvalid", DEFAULT_LONG).get());
        assertEquals(DEFAULT_LONG, observableProperties.longProperty("longKeyNull", DEFAULT_LONG).get());
    }

    @Test
    public void floatProperty() throws Exception {
        assertEquals(12.34F, observableProperties.floatProperty("floatKey", DEFAULT_FLOAT).get(), 0.0F);
        assertEquals(DEFAULT_FLOAT, observableProperties.floatProperty("floatKeyEmpty", DEFAULT_FLOAT).get(), 0.0F);
        assertEquals(DEFAULT_FLOAT, observableProperties.floatProperty("floatKeyInvalid", DEFAULT_FLOAT).get(), 0.0F);
        assertEquals(DEFAULT_FLOAT, observableProperties.floatProperty("floatKeyNull", DEFAULT_FLOAT).get(), 0.0F);
        assertEquals((Float) 1234.0F, (Float) observableProperties.floatProperty("floatKeyNoDecimal", DEFAULT_FLOAT).get());
    }

    @Test
    public void doubleProperty() throws Exception {
        assertEquals(1234.56, observableProperties.doubleProperty("doubleKey", DEFAULT_DOUBLE).get(), 0.0);
        assertEquals(DEFAULT_DOUBLE, observableProperties.doubleProperty("doubleKeyEmpty", DEFAULT_DOUBLE).get(), 0.0);
        assertEquals(DEFAULT_DOUBLE, observableProperties.doubleProperty("doubleKeyInvalid", DEFAULT_DOUBLE).get(), 0.0);
        assertEquals(DEFAULT_DOUBLE, observableProperties.doubleProperty("doubleKeyNull", DEFAULT_DOUBLE).get(), 0.0);
        assertEquals(123456.0, observableProperties.doubleProperty("doubleKeyNoDecimal", DEFAULT_DOUBLE).get(), 0.0);
    }

    @Test
    public void objectProperty() throws Exception {
        observableProperties.registerConverter(TestObject.class, new StringConverter<TestObject>() {
            @Override
            public String toString(TestObject object) {
                return object.integer + "-" + object.string;
            }

            @Override
            public TestObject fromString(String string) {
                final String[] split = string.split("-");
                return new TestObject(Integer.valueOf(split[0]), split[1]);
            }
        });
        assertEquals(new TestObject(1, "Eins"), observableProperties.objectProperty("objectKey", DEFAULT_TESTOBJECT).get());
    }

    @Test
    public void objectPropertyNoConverter() throws Exception {
        try {
            assertEquals(new TestObject(1, "Eins"), observableProperties.objectProperty("objectKey", DEFAULT_TESTOBJECT).get());
            fail("Exception expected!");
        } catch (final Exception e) {
            assertTrue(e.getMessage().startsWith("No StringConverter registered for"));
        }
    }

    private String getAbsoluteResourcePath(final String resourcePath) throws Exception {
        final File file = new File(getClass().getClassLoader().getResource(resourcePath).getFile());
        return file.getAbsolutePath();
    }


    private static class TestObject {
        private Integer integer;
        private String string;

        private TestObject(Integer integer, String string) {
            this.integer = integer;
            this.string = string;
        }

        @Override
        public boolean equals(Object obj) {
            TestObject o = (TestObject) obj;
            return integer.equals(o.integer) && string.equals(o.string);
        }
    }
}