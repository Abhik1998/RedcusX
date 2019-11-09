package com.example.marius.helpmesee.directions.presenter;


import com.example.marius.helpmesee.directions.model.PathDto;
import java.util.List;

/**
 * Created by Marius Olariu <mariuslucian.olariu@gmail.com>
 */
public interface PathFoundListener {

  /**
   * Callback method
   * @param pathDto - dto object
   */
  void onPathsFound(List<PathDto> pathDto);
}
