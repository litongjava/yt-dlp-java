package com.litongjava.yt.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class YtVideoEntry {

  private String _type;
  private String ie_key;
  private String id;
  private String url;
  private String title;
  private String description;

  private Long duration;

  private String channel_id;
  private String channel;
  private String channel_url;

  private String uploader;
  private String uploader_id;
  private String uploader_url;

  private Long timestamp;
  private Long release_timestamp;

  private String availability;

  private Long view_count;

  private Boolean channel_is_verified;

  private String __x_forwarded_for_ip;
  
}