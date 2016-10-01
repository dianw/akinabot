package akinabot.verticle.codec;

import org.nustaq.serialization.FSTConfiguration;

import com.pengrad.telegrambot.model.Update;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

public class UpdateCodec implements MessageCodec<Update, Update> {
	private static FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();
	static {
		conf.setForceSerializable(true);
	}
	
	@Override
	public void encodeToWire(Buffer buffer, Update s) {
		byte[] obj = conf.asByteArray(s);
		buffer.appendInt(obj.length);
		buffer.appendBytes(obj);
	}

	@Override
	public Update decodeFromWire(int pos, Buffer buffer) {
		int length = buffer.getInt(pos);
		pos += 4;
		byte[] obj = buffer.getBytes(pos, pos + length);
		
		return (Update) conf.asObject(obj);
	}

	@Override
	public Update transform(Update s) {
		return conf.deepCopy(s);
	}

	@Override
	public String name() {
		return getClass().getName();
	}

	@Override
	public byte systemCodecID() {
		return -1;
	}

}
