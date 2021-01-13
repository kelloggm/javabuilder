class User < ApplicationRecord
  validates_uniqueness_of :username

  def self.generate
    last_user = User.last
    username = "user_"
    user_number = 0
    if (last_user)
      user_number = last_user.id + 1
    end
    create(username: "#{username}#{user_number}")
  end
end
