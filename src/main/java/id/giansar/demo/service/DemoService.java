package id.giansar.demo.service;

import id.giansar.demo.dto.ProvinceDto;
import id.giansar.demo.dto.ResponseDto;
import org.simpleflatmapper.csv.CsvParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileReader;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DemoService {
    private static final Logger LOGGER = LoggerFactory.getLogger("DemoService");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public ResponseDto uploadFile(MultipartFile file) {
        try {
            Path destination = Paths.get(file.getOriginalFilename());
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
            }

            List<ProvinceDto> provinces = new ArrayList<>();
            if (Files.exists(destination)) {
                try (Reader reader = new FileReader(destination.toFile())) {
                    provinces = CsvParser.quote('\"').separator(',').mapTo(ProvinceDto.class).headers("id", "name").stream(reader).collect(Collectors.toList());
                }
                provinces.forEach(province -> LOGGER.info(province.id + " - " + province.name));
            }

            String dbVersion = jdbcTemplate.queryForObject("select version()", String.class);
            LOGGER.info(dbVersion);
            int[] i = insertProvince(provinces);
            LOGGER.info("Rows inserted = " + i.length);
            return new ResponseDto(200, "0000", "Success");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseDto(500, "0101", "There is an error");
        }
    }

    private int[] insertProvince(List<ProvinceDto> provinces) throws Exception {
        return jdbcTemplate.batchUpdate("INSERT INTO province (id, name) values (?, ?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                preparedStatement.setInt(1, provinces.get(i).id);
                preparedStatement.setString(2, provinces.get(i).name);
            }

            @Override
            public int getBatchSize() {
                return provinces.size();
            }
        });
    }
}
