require 'securerandom'

module ApplicationCable
  class Connection < ActionCable::Connection::Base
    identified_by :fake_id

    def connect
      self.fake_id = SecureRandom.uuid
    end
  end
end
