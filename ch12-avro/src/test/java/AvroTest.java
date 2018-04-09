import org.apache.avro.AvroTypeException;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.util.Utf8;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class AvroTest {

    @Test
    public void testInt() throws IOException {

        Schema schema = new Schema.Parser().parse("\"int\"");

        int datum = 163;

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DatumWriter<Integer> writer = new GenericDatumWriter<Integer>(schema);
        Encoder encoder = EncoderFactory.get().binaryEncoder(out, null /* reuse */);
        writer.write(datum, encoder); // boxed
        encoder.flush();
        out.close();

        DatumReader<Integer> reader = new GenericDatumReader<Integer>(schema); // have to tell it the schema - it's not in the data stream!
        Decoder decoder = DecoderFactory.get().binaryDecoder(out.toByteArray(), null /* reuse */);
        Integer result = reader.read(null /* reuse */, decoder);
        assertThat(result, is(163));

        try {
            reader.read(null, decoder);
            fail("Excepted EOFException");
        } catch (EOFException e) {
            // expected
        }
    }

    @Test
    public void testGenericString() throws IOException {

        Schema schema = new Schema.Parser().parse("{\"type\": \"string\", \"avro.java.string\": \"String\"}");

        String datum = "foo";

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DatumWriter<String> writer = new GenericDatumWriter<String>(schema);
        Encoder encoder = EncoderFactory.get().binaryEncoder(out, null /* reuse */);
        writer.write(datum, encoder); // boxed
        encoder.flush();
        out.close();

        DatumReader<String> reader = new GenericDatumReader<String>(schema);
        Decoder decoder = DecoderFactory.get().binaryDecoder(out.toByteArray(), null /* reuse */);
        String result = reader.read(null /* reuse */, decoder);
        assertThat(result, equalTo("foo"));

        try {
            reader.read(null, decoder);
            fail("Expected EOFException");
        } catch (EOFException e) {
            // expected
        }
    }

    @Test
    public void testPairGeneric() throws IOException {
        // Parse Schema
        Schema.Parser parser = new Schema.Parser();
        Schema schema = parser.parse(getClass().getResourceAsStream("StringPair.avsc"));

        // Record Creation
        GenericRecord datum = new GenericData.Record(schema);
        datum.put("left", "L");
        datum.put("right", "R");

        // Record Serialization
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(schema);
        Encoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        writer.write(datum, encoder);
        encoder.flush();
        out.close();

        // Record Deserialization
        DatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord>(schema);
        Decoder decoder = DecoderFactory.get().binaryDecoder(out.toByteArray(), null);
        GenericRecord result = reader.read(null, decoder);
        assertThat(result.get("left").toString(), is("L"));
        assertThat(result.get("right").toString(), is("R"));
    }

    @Test
    public void testPairSpecific() throws IOException {

        // Specific String Pair
        StringPair datum = new StringPair();
        datum.setLeft("L");
        datum.setRight("R");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DatumWriter<StringPair> writer = new SpecificDatumWriter<StringPair>(StringPair.class);
        Encoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        writer.write(datum, encoder);
        encoder.flush();
        out.close();

        DatumReader<StringPair> reader = new SpecificDatumReader<StringPair>(StringPair.class);
        Decoder decoder = DecoderFactory.get().binaryDecoder(out.toByteArray(), null);
        StringPair result = reader.read(null, decoder);
        assertThat(result.getLeft(), is("L"));
        assertThat(result.getRight(), is("R"));
    }

    @Test
    public void testDataFile() throws IOException {
        Schema schema = new Schema.Parser().parse(getClass().getResourceAsStream("StringPair.avsc"));

        GenericRecord datum = new GenericData.Record(schema);
        datum.put("left", "L");
        datum.put("right", "R");

        // File Creation
        File file = new File("data.avro");
        DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(schema);
        DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<GenericRecord>(writer);
        dataFileWriter.create(schema, file);
        dataFileWriter.append(datum);
        dataFileWriter.close();

        // File Get Schema
        DatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord>();
        DataFileReader<GenericRecord> dataFileReader = new DataFileReader<GenericRecord>(file, reader);
        assertThat("Schema is the same", schema, is(dataFileReader.getSchema()));

        // File Read
        assertThat(dataFileReader.hasNext(), is(true));
        GenericRecord result = dataFileReader.next();
        assertThat(result.get("left").toString(), is("L"));
        assertThat(result.get("right").toString(), is("R"));
        assertThat(dataFileReader.hasNext(), is(false));

        file.delete();
    }

    @Test
    public void testDataFileIteration() throws IOException {

        Schema schema = new Schema.Parser().parse(getClass().getResourceAsStream("StringPair.avsc"));

        GenericRecord datum = new GenericData.Record(schema);
        datum.put("left", "L");
        datum.put("right", "R");

        File file = new File("data.avro");
        DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(schema);
        DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<GenericRecord>(writer);
        dataFileWriter.create(schema, file);
        dataFileWriter.append(datum);
        datum.put("right", new Utf8("r"));
        dataFileWriter.append(datum);
        dataFileWriter.close();

        DatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord>();
        DataFileReader<GenericRecord> dataFileReader = new DataFileReader<GenericRecord>(file, reader);
        assertThat("Schema is the same", schema, is(dataFileReader.getSchema()));

        // DataFileIterator
        GenericRecord record = null;
        while(dataFileReader.hasNext()) {
            record = dataFileReader.next(record);
            // process record
        }

        dataFileReader = new DataFileReader<GenericRecord>(file, reader);
        int count = 0;
        record = null;
        while(dataFileReader.hasNext()) {
            record = dataFileReader.next(record);
            // process record
            count++;
            assertThat(record.get("left").toString(), is("L"));
            if(count == 1) {
                assertThat(record.get("right").toString(), is("R"));
            } else {
                assertThat(record.get("right").toString(), is("r"));
            }
        }

        assertThat(count, is(2));
        file.delete();
    }

    @Test
    public void testDataFileIterationShort() throws IOException {

        Schema schema = new Schema.Parser().parse(getClass().getResourceAsStream("StringPair.avsc"));

        GenericRecord datum = new GenericData.Record(schema);
        datum.put("left", "L");
        datum.put("right", "R");

        File file = new File("data.avro");
        DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(schema);
        DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<GenericRecord>(writer);
        dataFileWriter.create(schema, file);
        dataFileWriter.append(datum);
        datum.put("right", new Utf8("r"));
        dataFileWriter.append(datum);
        dataFileWriter.close();

        DatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord>();
        DataFileReader<GenericRecord> dataFileReader = new DataFileReader<GenericRecord>(file, reader);
        assertThat("Schema is the same", schema, is(dataFileReader.getSchema()));

        // DataFileShortIterator
        for(GenericRecord record : dataFileReader) {
            // process record
        }

        dataFileReader = new DataFileReader<GenericRecord>(file, reader);
        int count = 0;
        for(GenericRecord record : dataFileReader) {
            // process record
            count++;
            assertThat(record.get("left").toString(), is("L"));
            if(count == 1) {
                assertThat(record.get("right").toString(), is("R"));
            } else {
                assertThat(record.get("right").toString(), is("r"));
            }
        }

        assertThat(count, is(2));
        file.delete();
    }

    @Test
    public void testSchemaResolution() throws IOException {

        Schema schema = new Schema.Parser().parse(getClass().getResourceAsStream("StringPair.avsc"));
        Schema newSchema = new Schema.Parser().parse(getClass().getResourceAsStream("NewStringPair.avsc"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(schema);
        Encoder encoder = EncoderFactory.get().binaryEncoder(out, null /* reuse */);
        GenericRecord datum = new GenericData.Record(schema); // no description
        datum.put("left", "L");
        datum.put("right", "R");
        writer.write(datum, encoder);
        encoder.flush();

        // SchemaResolution
        DatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord>(schema, newSchema);
        Decoder decoder = DecoderFactory.get().binaryDecoder(out.toByteArray(), null);
        GenericRecord result = reader.read(null, decoder);
        assertThat(result.get("left").toString(), is("L"));
        assertThat(result.get("right").toString(), is("R"));
        assertThat(result.get("description").toString(), is(""));
    }

    @Test
    public void testSchemaResolutionWithAliases() throws IOException {

        Schema schema = new Schema.Parser().parse(getClass().getResourceAsStream("StringPair.avsc"));
        Schema newSchema = new Schema.Parser().parse(getClass().getResourceAsStream("AliasedStringPair.avsc"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(schema);
        Encoder encoder = EncoderFactory.get().binaryEncoder(out, null /* reuse */);
        GenericRecord datum = new GenericData.Record(schema); // no description
        datum.put("left", "L");
        datum.put("right", "R");
        writer.write(datum, encoder);
        encoder.flush();

        DatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord>(schema, newSchema);
        Decoder decoder = DecoderFactory.get().binaryDecoder(out.toByteArray(), null);
        GenericRecord result = reader.read(null, decoder);
        assertThat(result.get("first").toString(), is("L"));
        assertThat(result.get("second").toString(), is("R"));

        // old field names don't work
        assertThat(result.get("left"), is((Object) null));
        assertThat(result.get("right"), is((Object) null));
    }

    @Test
    public void testSchemaResolutionWithNull() throws IOException {

        Schema schema = new Schema.Parser().parse(getClass().getResourceAsStream("StringPair.avsc"));
        Schema newSchema = new Schema.Parser().parse(getClass().getResourceAsStream("NewStringPairWithNull.avsc"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(schema);
        Encoder encoder = EncoderFactory.get().binaryEncoder(out, null /* reuse */);
        GenericRecord datum = new GenericData.Record(schema); // no description
        datum.put("left", "L");
        datum.put("right", "R");
        writer.write(datum, encoder);
        encoder.flush();

        DatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord>(schema, newSchema);
        Decoder decoder = DecoderFactory.get().binaryDecoder(out.toByteArray(), null);
        GenericRecord result = reader.read(null, decoder);
        assertThat(result.get("left").toString(), is("L"));
        assertThat(result.get("right").toString(), is("R"));
        assertThat(result.get("description"), is((Object) null));
    }

    @Test
    public void testIncompatibleSchemaResolution() throws IOException {

        Schema schema = new Schema.Parser().parse(getClass().getResourceAsStream("StringPair.avsc"));
        Schema newSchema = new Schema.Parser().parse("{\"type\": \"array\", \"items\": \"string\"}");

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(schema);
        Encoder encoder = EncoderFactory.get().binaryEncoder(out, null /* reuse */);
        GenericRecord datum = new GenericData.Record(schema); // no description
        datum.put("left", "L");
        datum.put("right", "R");
        writer.write(datum, encoder);
        encoder.flush();

        DatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord>(schema, newSchema);
        Decoder decoder = DecoderFactory.get().binaryDecoder(out.toByteArray(), null);
        try {
            reader.read(null, decoder);
            fail("Expected AvroTypeException");
        } catch (AvroTypeException e) {
            // expected
        }
    }

    @Test
    public void testSchemaResolutionWithDataFile() throws IOException {

        Schema schema = new Schema.Parser().parse(getClass().getResourceAsStream("StringPair.avsc"));
        Schema newSchema = new Schema.Parser().parse(getClass().getResourceAsStream("NewStringPair.avsc"));

        File file = new File("data.avro");

        DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(schema);
        DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<GenericRecord>(writer);
        dataFileWriter.create(schema, file);
        GenericRecord datum = new GenericData.Record(schema);
        datum.put("left", "L");
        datum.put("right", "R");
        dataFileWriter.append(datum);
        dataFileWriter.close();

        // Resolution with data file
        DatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord>(/*[*/null/*]*/, newSchema);

        DataFileReader<GenericRecord> dataFileReader = new DataFileReader<GenericRecord>(file, reader);
        assertThat(schema, is(dataFileReader.getSchema()));

        assertThat(dataFileReader.hasNext(), is(true));
        GenericRecord result = dataFileReader.next();
        assertThat(result.get("left").toString(), is("L"));
        assertThat(result.get("right").toString(), is("R"));
        assertThat(result.get("description").toString(), is(""));
        assertThat(dataFileReader.hasNext(), is(false));

        file.delete();
    }
}
