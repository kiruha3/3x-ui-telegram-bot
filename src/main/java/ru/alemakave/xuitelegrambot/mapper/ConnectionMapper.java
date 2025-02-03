package ru.alemakave.xuitelegrambot.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.alemakave.xuitelegrambot.dto.ConnectionDTO;
import ru.alemakave.xuitelegrambot.model.Connection;
import ru.alemakave.xuitelegrambot.model.ConnectionAllocate;
import ru.alemakave.xuitelegrambot.model.ConnectionSettings;
import ru.alemakave.xuitelegrambot.model.ConnectionStreamSettings;
import ru.alemakave.xuitelegrambot.model.ConnectionSniffing;

@Mapper(componentModel = "spring",
        imports = {
                ObjectMapper.class,
                ConnectionSettings.class,
                ConnectionStreamSettings.class,
                ConnectionSniffing.class,
                ConnectionAllocate.class
})
public abstract class ConnectionMapper {
    @Mapping(target = "settings", expression = "java(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(connection.getSettings()))")
    @Mapping(target = "streamSettings", expression = "java(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(connection.getStreamSettings()))")
    @Mapping(target = "sniffing", expression = "java(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(connection.getSniffing()))")
    @Mapping(target = "allocate", expression = "java(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(connection.getAllocate()))")
    public abstract ConnectionDTO connectionToConnectionGetUpdateDto(Connection connection) throws JsonProcessingException;

    public Connection connectionGetUpdateDtoToConnection(ConnectionDTO connectionDto) throws JsonProcessingException {
        if ( connectionDto == null ) {
            return null;
        }

        Connection connection = new Connection();

        connection.setId( connectionDto.getId() );
        connection.setUp( connectionDto.getUp() );
        connection.setDown( connectionDto.getDown() );
        connection.setTotal( connectionDto.getTotal() );
        connection.setRemark( connectionDto.getRemark() );
        connection.setEnable( connectionDto.isEnable() );
        connection.setExpiryTime( connectionDto.getExpiryTime() );
        connection.setClientStats( connectionDto.getClientStats() );
        connection.setListen( connectionDto.getListen() );
        connection.setPort( connectionDto.getPort() );
        connection.setProtocol( connectionDto.getProtocol() );
        connection.setTag( connectionDto.getTag() );

        if (connectionDto.getSettings() != null) {
            connection.setSettings(new ObjectMapper().readValue(connectionDto.getSettings(), ConnectionSettings.class));
        }
        if (connectionDto.getStreamSettings() != null) {
            connection.setStreamSettings(new ObjectMapper().readValue(connectionDto.getStreamSettings(), ConnectionStreamSettings.class));
        }
        if (connectionDto.getSniffing() != null) {
            connection.setSniffing(new ObjectMapper().readValue(connectionDto.getSniffing(), ConnectionSniffing.class));
        }
        if (connectionDto.getAllocate() != null && !connectionDto.getAllocate().isEmpty()) {
            connection.setAllocate(new ObjectMapper().readValue(connectionDto.getAllocate(), ConnectionAllocate.class));
        }

        return connection;
    }
}
