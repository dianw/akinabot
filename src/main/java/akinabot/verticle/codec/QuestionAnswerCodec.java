package akinabot.verticle.codec;

import javax.inject.Inject;

import org.nustaq.serialization.FSTConfiguration;

import akinabot.model.bot.QuestionAnswer;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

public class QuestionAnswerCodec implements MessageCodec<QuestionAnswer, QuestionAnswer>{
	@Inject FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();
	
	@Override
	public void encodeToWire(Buffer buffer, QuestionAnswer s) {
		byte[] obj = conf.asByteArray(s);
		buffer.appendInt(obj.length);
		buffer.appendBytes(obj);
	}

	@Override
	public QuestionAnswer decodeFromWire(int pos, Buffer buffer) {
		int length = buffer.getInt(pos);
		pos += 4;
		byte[] obj = buffer.getBytes(pos, pos + length);
		
		return (QuestionAnswer) conf.asObject(obj);
	}

	@Override
	public QuestionAnswer transform(QuestionAnswer s) {
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
