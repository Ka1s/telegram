import com.arteeck.CommandParser;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

class Command222ParserTest {

    @Test
    void getCommandName() {
        CommandParser parser = new CommandParser("/echo hello");
        Assert.assertEquals("/echo", parser.getCommandName());
    }

    @Test
    void getCommandText() {
        CommandParser parser = new CommandParser("/echo hello");
        Assert.assertEquals("hello", parser.getCommandText());
    }
}