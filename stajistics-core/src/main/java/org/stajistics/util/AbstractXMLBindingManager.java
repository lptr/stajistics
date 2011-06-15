package org.stajistics.util;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * @author The Stajistics Project
 */
public abstract class AbstractXMLBindingManager<T> implements XMLBindingManager<T> {

    protected String encoding = DEFAULT_ENCODING;

    protected abstract Class<T> getRootElementType();

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(final String encoding) {
        if (encoding == null) {
            throw new NullPointerException("encoding");
        }

        this.encoding = encoding;
    }

    public T unmarshal(final String in) {
        return unmarshal(new StringReader(in));
    }

    public T unmarshal(final File in) {
        StreamSource src = new StreamSource(in);
        try {
            return unmarshal(src);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public T unmarshal(final Reader in) {
        StreamSource src = new StreamSource(in);
        try {
            return unmarshal(src);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public T unmarshal(final InputStream in) {
        StreamSource src = new StreamSource(in);
        try {
            return unmarshal(src);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public String marshal(final T binding) {
        StringWriter out = new StringWriter(4 * 1024);
        marshal(binding, out);
        return out.toString();
    }

    public void marshal(final T binding,
                        final OutputStream out) {
        StreamResult result = new StreamResult(out);
        try {
            marshal(binding, result);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public void marshal(final T binding,
                        final Writer out) {
        StreamResult result = new StreamResult(out);
        try {
            marshal(binding, result);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public void marshal(final T binding,
                        final File out) {
        StreamResult result = new StreamResult(out);
        try {
            marshal(binding, result);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    protected JAXBContext getJAXBContext() throws JAXBException {
        return JAXBContext.newInstance(getRootElementType().getPackage().getName());
    }

    protected T unmarshal(final StreamSource source) throws JAXBException {
        Unmarshaller unmarshaller = getJAXBContext().createUnmarshaller();
        JAXBElement<T> root = unmarshaller.unmarshal(source, getRootElementType());
        T result = root.getValue();
        return result;
    }

    protected void marshal(final T binding,
                           final StreamResult out) throws JAXBException {
        Marshaller marshaller = getJAXBContext().createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);

        marshaller.marshal(binding, out);
    }

}
