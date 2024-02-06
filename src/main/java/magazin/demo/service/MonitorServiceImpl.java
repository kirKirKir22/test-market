package magazin.demo.service;

import magazin.demo.dto.MonitorsDTO;
import magazin.demo.entity.MonitorEntity;
import magazin.demo.exception.monitors.MonitorAlreadyExistsException;
import magazin.demo.exception.monitors.MonitorNotFoundException;
import magazin.demo.mapper.MonitorsMapper;
import magazin.demo.repository.MonitorRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MonitorServiceImpl implements MonitorService {

    private final MonitorRepository monitorRepository;
    private final MonitorsMapper monitorsMapper;

    public MonitorServiceImpl(MonitorRepository monitorRepository, MonitorsMapper monitorsMapper) {
        this.monitorRepository = monitorRepository;
        this.monitorsMapper = monitorsMapper;
    }

    @Override
    public MonitorsDTO createMonitor(MonitorsDTO monitorDTO) throws MonitorAlreadyExistsException {
        try {
            if (monitorRepository.existsBySerialNumber(monitorDTO.getSerialNumber())) {
                throw new MonitorAlreadyExistsException("Монитор с таким серийным номером уже существует");
            }
            MonitorEntity monitorEntity = monitorsMapper.dtoToEntity(monitorDTO);
            MonitorEntity savedEntity = monitorRepository.save(monitorEntity);
            return monitorsMapper.entityToDto(savedEntity);
        } catch (DataIntegrityViolationException e) {

            throw new MonitorAlreadyExistsException("Монитор с таким серийным номером уже существует");
        }
    }

    @Override
    public MonitorsDTO readMonitorById(long id) throws MonitorNotFoundException {
        Optional<MonitorEntity> monitorEntityOptional = monitorRepository.findById(id);

        if (monitorEntityOptional.isPresent()) {
            MonitorEntity monitorEntity = monitorEntityOptional.get();
            return monitorsMapper.entityToDto(monitorEntity);
        } else {
            throw new MonitorNotFoundException("Монитор с id " + id + " не найден");
        }
    }

    @Override
    public List<MonitorsDTO> findAllMonitors() {
        List<MonitorEntity> monitorEntities = monitorRepository.findAll();

        List<MonitorsDTO> monitorDTOs = monitorEntities.stream()
                .map(monitorsMapper::entityToDto)
                .collect(Collectors.toList());

        return monitorDTOs;
    }

    @Override
    public MonitorsDTO updateMonitor(long id, MonitorsDTO updatedMonitorDTO) throws MonitorNotFoundException {
        Optional<MonitorEntity> optionalMonitor = monitorRepository.findById(id);

        if (optionalMonitor.isPresent()) {
            MonitorEntity existingMonitor = optionalMonitor.get();

            existingMonitor.setSerialNumber(updatedMonitorDTO.getSerialNumber());
            existingMonitor.setManufacturer(updatedMonitorDTO.getManufacturer());
            existingMonitor.setPrice(updatedMonitorDTO.getPrice());
            existingMonitor.setQuantity(updatedMonitorDTO.getQuantity());
            existingMonitor.setDiagonal(updatedMonitorDTO.getDiagonal());

            MonitorEntity updatedMonitorEntity = monitorRepository.save(existingMonitor);

            return monitorsMapper.entityToDto(updatedMonitorEntity);
        } else {
            throw new MonitorNotFoundException("Монитор с id " + id + " не найден");
        }
    }

    @Override
    public void deleteMonitor(long id) throws MonitorNotFoundException {
        Optional<MonitorEntity> optionalMonitor = monitorRepository.findById(id);

        if (optionalMonitor.isPresent()) {
            monitorRepository.delete(optionalMonitor.get());
        } else {
            throw new MonitorNotFoundException("Монитор с id " + id + " не найден");
        }
    }
}